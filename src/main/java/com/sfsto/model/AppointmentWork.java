package com.sfsto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment_works")
public class AppointmentWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "work_option_id")
    private WorkOption workOption;

    // optional override duration in minutes (if needed in future)
    private Integer durationOverride;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public WorkOption getWorkOption() { return workOption; }
    public void setWorkOption(WorkOption workOption) { this.workOption = workOption; }
    public Integer getDurationOverride() { return durationOverride; }
    public void setDurationOverride(Integer durationOverride) { this.durationOverride = durationOverride; }
}
