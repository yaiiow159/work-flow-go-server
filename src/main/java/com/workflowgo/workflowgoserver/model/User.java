package com.workflowgo.workflowgoserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;
    
    private String password;
    
    private String googleId;
    
    private String photoUrl;
    
    @Embedded
    private Preferences preferences;
    
    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Preferences {
        @Embedded
        private Theme theme;
        
        @Embedded
        private Notifications notifications;
        
        @Embedded
        private Display display;
    }
    
    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Theme {
        private boolean darkMode;
        private String primaryColor;
    }
    
    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Notifications {
        private boolean enabled;
        private boolean emailNotifications;
        private String reminderTime;
    }
    
    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Display {
        @Enumerated(EnumType.STRING)
        private DefaultView defaultView;
        private boolean compactMode;
        
        public enum DefaultView {
            CALENDAR,
            LIST
        }
    }

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
