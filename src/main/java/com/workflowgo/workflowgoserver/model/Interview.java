package com.workflowgo.workflowgoserver.model;

import com.workflowgo.workflowgoserver.model.converter.InterviewStatusConverter;
import com.workflowgo.workflowgoserver.model.converter.InterviewTypeConverter;
import com.workflowgo.workflowgoserver.model.enums.InterviewStatus;
import com.workflowgo.workflowgoserver.model.enums.InterviewType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Table(name = "interviews")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Interview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String companyName;
    
    @Column(nullable = false)
    private String position;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private LocalTime time;

    @Convert(converter = InterviewTypeConverter.class)
    private InterviewType type;

    @Convert(converter = InterviewStatusConverter.class)
    private InterviewStatus status;
    
    private String location;
    
    @Column(length = 2000)
    private String notes;
    
    @Embedded
    private ContactPerson contactPerson;
    
    private Integer rating;
    
    @Column(length = 2000)
    private String feedback;
    
    @CreationTimestamp
    private ZonedDateTime createdAt;
    
    @UpdateTimestamp
    private ZonedDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;
    
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Question> questions = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "interview_documents",
        joinColumns = @JoinColumn(name = "interview_id"),
        inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    @ToString.Exclude
    private Set<Document> documents = new HashSet<>();
    
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Reminder> reminders = new ArrayList<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Interview interview = (Interview) o;
        return getId() != null && Objects.equals(getId(), interview.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
