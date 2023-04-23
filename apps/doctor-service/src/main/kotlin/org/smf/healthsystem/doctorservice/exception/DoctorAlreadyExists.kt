package org.smf.healthsystem.doctorservice.exception

class DoctorAlreadyExists(doctorID: String) : RuntimeException("Doctor already exists $doctorID")
