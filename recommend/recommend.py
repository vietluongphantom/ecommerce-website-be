import mysql.connector
from mlxtend.frequent_patterns import apriori, association_rules
from mlxtend.preprocessing import TransactionEncoder
import pandas as pd
from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Cho phép cross-origin requests

def get_database_connection():
    """
    Thiết lập kết nối đến cơ sở dữ liệu MySQL
    """
    try:
        connection = mysql.connector.connect(
            host='localhost',          # Địa chỉ máy chủ MySQL
            port=3306,                 # Cổng MySQL 
            user='root',               # Tên đăng nhập MySQL
            password='kien0702',       # Mật khẩu MySQL 
            database='ecommercedb'     # Tên cơ sở dữ liệu
        )
        return connection
    except mysql.connector.Error as err:
        print(f"❌ Không thể kết nối database: {err}")
        raise

def get_order_data():
    """
    Kiểm tra kết nối và trích xuất dữ liệu đơn hàng từ cơ sở dữ liệu MySQL
    """
    connection = None
    try:
        # Kết nối đến cơ sở dữ liệu
        connection = get_database_connection()
        
        # Kiểm tra kết nối
        if connection.is_connected():
            print("✅ Kết nối database thành công!")
            
            cursor = connection.cursor()
            
            # Truy vấn kiểm tra dữ liệu orders
            cursor.execute("SELECT COUNT(*) FROM orders")
            order_count = cursor.fetchone()[0]
            print(f"✅ Tổng số đơn hàng: {order_count}")
            
            # Truy vấn kiểm tra dữ liệu order_item
            cursor.execute("SELECT COUNT(*) FROM order_item")
            order_item_count = cursor.fetchone()[0]
            print(f"✅ Tổng số mục đơn hàng: {order_item_count}")
            
            # Truy vấn lấy dữ liệu đơn hàng chi tiết
            query = """
            SELECT 
    o.id AS order_id, 
    GROUP_CONCAT(pi.product_id ORDER BY pi.product_id SEPARATOR ',') AS product_items
FROM 
    orders o
JOIN 
    order_item oi ON o.id = oi.order_id
JOIN 
    product_item pi ON oi.product_item_id = pi.id
GROUP BY 
    o.id
            """
            cursor.execute(query)
            
            # Lấy và in ra một số dòng dữ liệu
            results = cursor.fetchall()
            print("\n📋 10 dòng dữ liệu đầu tiên:")
            print("order_id | product_items")
            print("-" * 35)
            for row in results[:10]:
                print(f"{row[0]} | {row[1]}")
            
            # Nhóm sản phẩm theo đơn hàng
            orders = []
            for (order_id, product_items) in results:
                # Chuyển danh sách product_items (chuỗi) thành danh sách các sản phẩm
                items = product_items.split(",") if product_items else []
                orders.append(items)
            
            # In ma trận 2 chiều
            print("\n📋 Ma trận 2 chiều (danh sách các sản phẩm theo từng đơn hàng):")
            for row in orders:
                print(row)
            
            return orders
        
    except mysql.connector.Error as err:
        print(f"❌ Lỗi kết nối database: {err}")
        return []
    
    finally:
        # Đóng kết nối
        if connection and connection.is_connected():
            connection.close()
            print("✅ Đã đóng kết nối database")

def create_recommendation_rules(data):
    """
    Tạo quy tắc gợi ý sản phẩm từ dữ liệu đơn hàng
    """
    if not data:
        print("❌ Không có dữ liệu để tạo quy tắc!")
        return pd.DataFrame()  # Trả về DataFrame rỗng

    # Chuyển đổi tất cả các item sang string để đảm bảo tính nhất quán
    data = [[str(item) for item in order] for order in data]

    # Sử dụng TransactionEncoder để chuyển đổi dữ liệu
    te = TransactionEncoder()
    te_ary = te.fit(data).transform(data)
    df = pd.DataFrame(te_ary, columns=te.columns_)

    # Tìm tập mục thường xuyên với ngưỡng support thấp hơn
    print("🔍 Đang tìm tập mục thường xuyên...")
    frequent_itemsets = apriori(df, min_support=0.01, use_colnames=True)
    
    # Kiểm tra frequent_itemsets
    if frequent_itemsets.empty:
        print("❌ Không tìm thấy tập mục thường xuyên!")
        return pd.DataFrame()

    # Tạo quy tắc kết hợp
    print("🔍 Đang tạo quy tắc kết hợp...")
    rules = association_rules(
        frequent_itemsets, 
        metric="confidence", 
        min_threshold=0.01,  # Giảm ngưỡng confidence
        num_itemsets=2
    )
    
    print(f"✅ Đã tạo {len(rules)} quy tắc kết hợp")
    return rules

def recommend_products(product_id, rules, top_n=5):
    """
    Tạo gợi ý sản phẩm dựa trên quy tắc kết hợp
    """
    # Chuyển đổi product_id sang string
    product_id = str(product_id)

    # Kiểm tra rules có rỗng không
    if rules.empty:
        print("❌ Không có quy tắc để tạo recommendation")
        return []

    # Debug: In thông tin chi tiết
    print(f"🔍 Tìm recommendation cho product_id: {product_id}")
    print(f"📊 Tổng số rules: {len(rules)}")

    # Lọc các luật có sản phẩm đầu vào trong tập antecedents
    related_rules = rules[rules['antecedents'].apply(lambda x: product_id in x)]
    
    print(f"📊 Số lượng related_rules: {len(related_rules)}")
    
    if related_rules.empty:
        print(f"❌ Không tìm thấy rules liên quan đến product_id {product_id}")
        return []

    # Sắp xếp theo độ tin cậy giảm dần
    related_rules = related_rules.sort_values('confidence', ascending=False)

    # Lấy danh sách sản phẩm gợi ý
    recommendations = []
    for _, rule in related_rules.iterrows():
        for item in rule['consequents']:
            if item != product_id and item not in recommendations:
                recommendations.append(item)
                if len(recommendations) == top_n:
                    break
        if len(recommendations) == top_n:
            break
    
    print(f"✅ Các sản phẩm được gợi ý: {recommendations}")
    return recommendations


def get_product_details(recommended_product_ids):
    """
    Lấy chi tiết sản phẩm từ database MySQL
    """
    connection = None
    try:
        # Kết nối đến cơ sở dữ liệu
        connection = get_database_connection()
        
        if connection.is_connected():
            cursor = connection.cursor(dictionary=True)
            
            # Chuyển đổi danh sách product_ids sang chuỗi để truy vấn
            product_ids_str = ','.join(recommended_product_ids)
            
            # Truy vấn lấy thông tin chi tiết sản phẩm
            query = f"""
            SELECT 
                p.id AS product_id, 
                p.name, 
                p.min_price,
                COALESCE(r.average_stars, 0) AS average_rating,
                r.quantity AS rating_quantity
            FROM 
                product p
            LEFT JOIN 
                rate r ON p.id = r.product_id
            WHERE 
                p.id IN ({product_ids_str})
            """
            
            cursor.execute(query)
            product_details = cursor.fetchall()
            
            # Bổ sung hình ảnh chi tiết
            for product in product_details:
                # Truy vấn lấy hình ảnh của sản phẩm
                image_query = f"""
                SELECT link 
                FROM images 
                WHERE product_id = {product['product_id']} 
                LIMIT 3
                """
                cursor.execute(image_query)
                images = cursor.fetchall()
                product['images'] = [img['link'] for img in images]
            
            return product_details
    
    except mysql.connector.Error as err:
        print(f"❌ Lỗi truy vấn chi tiết sản phẩm: {err}")
        return []
    
    finally:
        # Đóng kết nối
        if connection and connection.is_connected():
            connection.close()

# Khởi tạo các quy tắc recommendation toàn cục
global_data = get_order_data()
global_rules = create_recommendation_rules(global_data)

@app.route('/recommend', methods=['GET'])
def get_recommendations():
    """
    Endpoint API để lấy gợi ý sản phẩm chi tiết
    """
    product_id = request.args.get('product_id', default='1', type=str)
    top_n = request.args.get('top_n', default=5, type=int)
    
    try:
        # Lấy danh sách product_ids được gợi ý
        recommended_product_ids = recommend_products(product_id, global_rules, top_n)
        
        # Lấy chi tiết sản phẩm
        product_details = get_product_details(recommended_product_ids)
        
        return jsonify({
            'product_id': product_id,
            'recommendations': product_details
        })
    except Exception as e:
        print(f"❌ Lỗi trong quá trình tạo recommendation: {e}")
        return jsonify({'error': str(e)}), 400

# Cấu hình để chạy ứng dụng
if __name__ == '__main__':
    app.run(debug=True, port=3001)