package com.workflowgo.workflowgoserver.model;

import com.workflowgo.workflowgoserver.model.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    private String photoURL;
    
    @Lob
    @Column(name = "photo_content", columnDefinition = "oid")
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    private byte[] photoContent;
    
    @Column(nullable = false)
    private Boolean emailVerified = false;
    
    private String password;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    
    private String providerId;

    @Column(length = 1000)
    private String bio;
    
    private String phone;
    
    private String location;
    
    private String company;
    
    private String position;

    @Embedded
    private UserPreferences preferences = new UserPreferences();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Interview> interviews = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Document> documents = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Notification> notifications = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
