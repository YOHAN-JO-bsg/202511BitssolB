package com.example.sideproject01.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // 실제 테이블명
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // 추가적으로 email, role 등 필요한 필드를 여기에 추가할 수 있습니다.
    // 예: private String email;
    // 예: private String role;
}
