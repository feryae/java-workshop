package com.pm.patientservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;

// All Business Logic and DTO Conversion happens in this

@Service
public class PatientService {
    private PatientRepository patientRepository;
    // gRPC
    private final BillingServiceGrpcClient billingServiceGrpcClient;

    // Dependency Injection
    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient){
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        // Convert patient (domain entity model) to patientResponseDTO object

        // Iterate over each patient list 
        // for each patient call the PatientMapper.toDTO method

        List<PatientResponseDTO> patientResponseDTOs = 
            patients.stream().map(PatientMapper::toDTO).toList();    
            // same as :
            // patients.stream().map(patient -> PatientMapper.toDTO(patient)).toList();
            
        return patientResponseDTOs;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        // email address must be unique (business logic) from PatientRepository
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient of this email "+ "already exists" + patientRequestDTO.getEmail());
        }


        // Save is a JPA method to save to the database
        Patient newPatient = patientRepository
            .save(PatientMapper.toModel(patientRequestDTO));
        // return to DTO and pass it to the controller

        // gRPC call if new patient goes well
        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), 
            newPatient.getName(), newPatient.getEmail());

        
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        // get patient by its id
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)){
            throw new EmailAlreadyExistsException("A patient of this email "+ "already exists" + patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);

    }

    public void deletePatient(UUID id){
        patientRepository.deleteById(id);
    }
}
