package org.smf.healthsystem.patientservice.exception

class PatientAlreadyExists(patientID: String) : RuntimeException("Patient already exists $patientID")
