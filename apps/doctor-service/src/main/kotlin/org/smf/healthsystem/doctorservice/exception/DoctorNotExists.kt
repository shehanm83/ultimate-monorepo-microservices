package org.smf.healthsystem.doctorservice.exception

class DoctorNotExists(doctorID: String) : RuntimeException("Doctor not exists $doctorID")
