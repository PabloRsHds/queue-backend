package br.com.queue.entities.serviceManagement;

import br.com.queue.entities.ticket.Ticket;
import br.com.queue.entities.user.User;
import br.com.queue.entities.department.Department;
import br.com.queue.entities.schedule.Schedule;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "tb_service_management ")
public class ServiceManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_management_id")
    private String serviceManagementId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "serviceManagement", fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "serviceManagement", fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();

    @ManyToMany(mappedBy = "services")
    private Set<User> users = new HashSet<>();

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
