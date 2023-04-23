package org.smf.healthsystem.patientservice.exception

class PatientNotExists(patientID: String) : RuntimeException("Patient not exists $patientID")
