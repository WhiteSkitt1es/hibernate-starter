package com.artemyev.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
//Transaction Lock
//@OptimisticLocking(type = OptimisticLockType.VERSION)
//where version = ?
//@OptimisticLocking(type = OptimisticLockType.ALL)
//@DynamicUpdate
//where all field = ?
//@OptimisticLocking(type = OptimisticLockType.DIRTY)
//@DynamicUpdate
//where update field = ?
public class Payment implements BaseEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Version
//    private Long version;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;
}
