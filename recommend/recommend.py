import mysql.connector
from mlxtend.frequent_patterns import apriori, association_rules
from mlxtend.preprocessing import TransactionEncoder
import pandas as pd
from flask import Flask, request, jsonify
from flask_cors import CORS
from collections import defaultdict
from itertools import combinations
app = Flask(__name__)
CORS(app)  # Cho phép cross-origin requests



def generate_frequent_itemsets(transactions, min_support):
    # Chuyển đổi tất cả items sang string
    transactions = [[str(item) for item in transaction] for transaction in transactions]
     
     # Number of transactions
    n_transactions = len(transactions)
    print(f"\nTotal number of transactions: {n_transactions}")

    # Số lượng giao dịch
    n_transactions = len(transactions)
    
    # Bước 1: Tìm frequent 1-itemsets
    item_counts = defaultdict(int)
    for transaction in transactions:
        for item in transaction:
            item_counts[frozenset([item])] += 1
    
    # Lọc các items có support >= min_support
    min_support_count = min_support * n_transactions
    frequent_itemsets = {
        k: v/n_transactions 
        for k, v in item_counts.items() 
        if v >= min_support_count
    }

    # Print frequent 1-itemsets with their support values
    print("\nFrequent 1-itemsets and their support values:")
    for item_set, support in frequent_itemsets.items():
        print(f"Item {list(item_set)[0]}: support = {support:.3f}")

    
    # Bước 2: Lặp để tìm frequent k-itemsets
    k = 2
    while True:
        print(f"\n=== Finding {k}-itemsets ===")
        # Tạo candidate k-itemsets từ frequent (k-1)-itemsets
        candidates = set()
        prev_frequent_items = list(frequent_itemsets.keys())
        
        print(f"\nGenerating candidates from {len(prev_frequent_items)} previous frequent itemsets...")

        for i in range(len(prev_frequent_items)):
            for j in range(i+1, len(prev_frequent_items)):
                items1 = set(prev_frequent_items[i])
                items2 = set(prev_frequent_items[j])
                union = items1.union(items2)
                if len(union) == k:
                    candidates.add(frozenset(union))
        
        print(f"Generated {len(candidates)} candidate {k}-itemsets:")
        for candidate in candidates:
            print(f"Candidate: {set(candidate)}")

        if not candidates:
            print(f"\nNo {k}-itemset candidates generated. Stopping.")
            break
            
        # Đếm support cho các candidate
        candidate_counts = defaultdict(int)
        for transaction in transactions:
            transaction_set = set(transaction)
            for candidate in candidates:
                if candidate.issubset(transaction_set):
                    candidate_counts[candidate] += 1
        
        # Thêm frequent k-itemsets vào kết quả
        k_frequent = {
            k: v/n_transactions 
            for k, v in candidate_counts.items() 
            if v >= min_support_count
        }
        
        print(f"\nFound {len(k_frequent)} frequent {k}-itemsets:")
        for itemset, support in k_frequent.items():
            print(f"Itemset {set(itemset)}: support = {support:.3f}")
        
        if not k_frequent:
            print(f"\nNo frequent {k}-itemsets found. Stopping.")
            break
            
        frequent_itemsets.update(k_frequent)
        k += 1
    
    print("\n=== Final Results ===")
    print(f"Total number of frequent itemsets found: {len(frequent_itemsets)}")
    print("\nAll frequent itemsets with their support values:")
    for itemset, support in frequent_itemsets.items():
        print(f"Itemset {set(itemset)}: support = {support:.3f}")

    return frequent_itemsets

def generate_association_rules(frequent_itemsets, min_confidence):

    print("\n=== BẮT ĐẦU TẠO LUẬT KẾT HỢP ===")
    print(f"Số lượng tập phổ biến đầu vào: {len(frequent_itemsets)}")
    print(f"Ngưỡng độ tin cậy tối thiểu: {min_confidence}")
    
    rules = []
    rule_count = 0
    
    for itemset, support in frequent_itemsets.items():
        if len(itemset) < 2:
            continue
            
        print(f"\n Đang xử lý tập phổ biến: {set(itemset)} (support = {support:.3f})")
        
        # Tạo tất cả các subset có thể làm antecedent
        for i in range(1, len(itemset)):
            
            for antecedent in combinations(itemset, i):
                antecedent = frozenset(antecedent)
                consequent = itemset - antecedent
                
                print(f"\n    Kiểm tra luật: {set(antecedent)} => {set(consequent)}")
                
                # Tính confidence
                if antecedent in frequent_itemsets:
                    confidence = support / frequent_itemsets[antecedent]
                    print(f"       Độ tin cậy = {confidence:.3f}")
                    
                    if confidence >= min_confidence:
                        rule_count += 1
                        print(f"       Luật thỏa mãn điều kiện!")
                        rule = {
                            'antecedents': set(antecedent),
                            'consequents': set(consequent),
                            'support': support,
                            'confidence': confidence
                        }
                        rules.append(rule)
                    else:
                        print(f"       Độ tin cậy thấp hơn ngưỡng {min_confidence}")
                else:
                    print("       Tiền đề không nằm trong tập phổ biến")
    
    print("\n=== KẾT QUẢ CUỐI CÙNG ===")
    print(f"Tổng số luật tìm được: {rule_count}")
    
    if rules:
        print("\nDanh sách các luật kết hợp (sắp xếp theo độ tin cậy):")
        sorted_rules = sorted(rules, key=lambda x: x['confidence'], reverse=True)
        for idx, rule in enumerate(sorted_rules, 1):
            print(f"\nLuật {idx}:")
            print(f"  Nếu mua: {rule['antecedents']}")
            print(f"  Thì sẽ mua: {rule['consequents']}")
            print(f"  Support: {rule['support']:.3f}")
            print(f"  Confidence: {rule['confidence']:.3f}")
    
    return rules

def get_database_connection():
    """
    Thiết lập kết nối đến cơ sở dữ liệu MySQL
    """
    try:
        connection = mysql.connector.connect(
            host='localhost',          
            port=3306,                
            user='root',              
            password='kien0702',       
            database='ecommercedb'     
        )
        return connection
    except mysql.connector.Error as err:
        print(f" Không thể kết nối database: {err}")
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

    if not data:
        print(" Không có dữ liệu để tạo quy tắc!")
        return []

    print(" Đang tìm tập mục thường xuyên...")
    frequent_itemsets = generate_frequent_itemsets(data, min_support=0.01)
    
    if not frequent_itemsets:
        print(" Không tìm thấy tập mục thường xuyên!")
        return []

    print(" Đang tạo quy tắc kết hợp...")
    rules = generate_association_rules(frequent_itemsets, min_confidence=0.01)
    
    print(f"✅ Đã tạo {len(rules)} quy tắc kết hợp")
    return rules

def recommend_products(product_id, rules, top_n=5):
    # Chuyển đổi product_id sang string
    product_id = str(product_id)
    print(f"\n=== TÌM GỢI Ý CHO SẢN PHẨM ID: {product_id} ===")
    # Kiểm tra rules có rỗng không
    if not rules:
        print(" Không có quy tắc để tạo recommendation")
        return []
    # Lọc các luật có sản phẩm đầu vào trong tập antecedents
    related_rules = [
        rule for rule in rules 
        if product_id in rule['antecedents']
    ]
    print(f" Số lượng related_rules: {len(related_rules)}")
    if not related_rules:
        print(f" Không tìm thấy rules liên quan đến product_id {product_id}")
        return []
    # Sắp xếp theo độ tin cậy giảm dần
    related_rules.sort(key=lambda x: x['confidence'], reverse=True)
    # Lấy danh sách sản phẩm gợi ý
    recommendations = []
    for rule in related_rules:
        for item in rule['consequents']:
            if item != product_id and item not in recommendations:
                recommendations.append(item)
                if len(recommendations) == top_n:
                    break
        if len(recommendations) == top_n:
            break
    print(f" Các sản phẩm được gợi ý: {recommendations}")
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