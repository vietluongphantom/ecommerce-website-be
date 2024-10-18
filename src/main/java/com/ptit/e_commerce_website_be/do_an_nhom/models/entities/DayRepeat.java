//package com.ghtk.ecommercewebsite.models.entities;
//
//import jakarta.persistence.Entity;
//
//@Entity
//public class DayRepeat {
//}
package com.ghtk.ecommercewebsite.models.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "day_repeat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayRepeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;

    public enum DayOfWeek {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY
    }
}
