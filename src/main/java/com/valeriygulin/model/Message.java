package com.valeriygulin.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "message")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NonNull
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
