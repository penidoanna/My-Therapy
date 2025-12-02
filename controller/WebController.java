package com.mycompany.mytherapy.controller;

import com.mycompany.mytherapy.model.Appointment;
import com.mycompany.mytherapy.model.Patient;
import com.mycompany.mytherapy.model.Psychologist;
import com.mycompany.mytherapy.service.AppointmentService;
import com.mycompany.mytherapy.service.PatientService;
import com.mycompany.mytherapy.service.PsychologistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import com.mycompany.mytherapy.model.AvailabilitySlot;
import com.mycompany.mytherapy.model.Resource;
import com.mycompany.mytherapy.service.AvailabilitySlotService;
import com.mycompany.mytherapy.service.ResourceService;
import jakarta.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.util.StringUtils.capitalize;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class WebController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PsychologistService psychologistService;

    @Autowired
    private AvailabilitySlotService availabilitySlotService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ResourceService resourceService;

    @GetMapping
    public String index(Model model) {
        // Get counts for dashboard
        long appointmentCount = appointmentService.listAll().size();
        long patientCount = patientService.listAll().size();
        long psychologistCount = psychologistService.listAll().size();

        model.addAttribute("appointmentCount", appointmentCount);
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("psychologistCount", psychologistCount);

        return "index";
    }

    // Appointments
    @GetMapping("/appointments")
    public String patientAppointments(Authentication authentication, Model model) {

        // Get logged-in patient
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);

        // Patient can see their own appointments
        List<Appointment> appointments
                = appointmentService.listByPatientId(patient.getId());

        model.addAttribute("appointments", appointments);
        model.addAttribute("patient", patient);

        return "appointments";
    }

    @GetMapping("/appointments/create")
    public String createAppointment(Model model) {
        List<Patient> patients = patientService.listAll();
        List<Psychologist> psychologists = psychologistService.listAll();

        model.addAttribute("patients", patients);
        model.addAttribute("psychologists", psychologists);
        model.addAttribute("appointment", new Appointment());
        return "create-appointment";
    }

    // Show appointment booking form
    @GetMapping("/appointments/book")
    public String showBookingForm(@RequestParam Long psychologistId,
            @RequestParam String selectedDay,
            @RequestParam String selectedTime,
            Authentication authentication,
            Model model) {

        Psychologist psychologist = psychologistService.findById(psychologistId)
                .orElseThrow(() -> new IllegalArgumentException("Psychologist not found"));

        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);

        model.addAttribute("psychologist", psychologist);
        model.addAttribute("patient", patient);
        model.addAttribute("selectedDay", selectedDay);
        model.addAttribute("selectedTime", selectedTime);
        model.addAttribute("appointment", new Appointment());

        return "book-appointment";
    }

    // Patients
    @GetMapping("/patients")
    public String patients(Model model, Authentication authentication) {
        System.out.println("DEBUG: Loading patients page for psychologist");

        // Get the logged-in psychologist
        String email = authentication.getName();
        System.out.println("DEBUG: Psychologist email: " + email);

        Psychologist psychologist = psychologistService.findByEmail(email);
        System.out.println("DEBUG: Found psychologist: " + (psychologist != null ? psychologist.getfName() : "NULL"));

        if (psychologist == null) {
            System.out.println("DEBUG: Psychologist not found, redirecting to login");
            return "redirect:/login";
        }

        // Get only the psychologist's own patients
        List<Patient> patients = patientService.findByPsychologist(psychologist);
        System.out.println("DEBUG: Found " + patients.size() + " patients");

        for (Patient patient : patients) {
            System.out.println("DEBUG: Patient: " + patient.getfName() + " " + patient.getlName());
        }

        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/patients/create")
    public String createPatient(Model model) {
        List<Psychologist> psychologists = psychologistService.listAll();
        model.addAttribute("psychologists", psychologists);
        model.addAttribute("patient", new Patient());
        return "create-patient";
    }

    @GetMapping("/patient/dashboard")
    public String patientDashboard(Authentication authentication, Model model) {

        String email = authentication.getName();  // logged-in patient email
        Patient patient = patientService.findByEmail(email);

        // Get upcoming appointments for this patient
        List<Appointment> appointments = appointmentService.listByPatientId(patient.getId());

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);

        return "patient-dashboard";
    }

    @GetMapping("/patient/profile/edit")
    public String showEditProfile(Model model, Authentication authentication) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);

        model.addAttribute("patient", patient);
        return "patient-edit-profile";
    }

    // Stripe Payment Method Management
    @GetMapping("/patient/payment-method")
    public String showPaymentMethodForm(Model model, Authentication authentication) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);

        // In a real application, you would check if the patient has a Stripe Customer ID.
        // If not, you would create one and save it to the patient record. For this mock implementation, we will ensure the patient has a mock ID.
        if (patient.getStripeCustomerId() == null || patient.getStripeCustomerId().isEmpty()) {
            // Mocking the creation of a Stripe Customer ID
            String mockCustomerId = "cus_" + patient.getId() + "_" + System.currentTimeMillis();
            patientService.updateStripeCustomerId(patient, mockCustomerId);
        }

        // In a real application, you would use the Stripe SDK to create a SetupIntent
        // and pass its client_secret to the model. For now, we will use a mock clientSecret.
        model.addAttribute("clientSecret", "seti_mock_client_secret_for_" + patient.getStripeCustomerId());

        return "patient-payment-method";
    }

    // Psychologists
    @GetMapping("/psychologists")
    public String psychologists(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String search,
            Model model) {

        List<Psychologist> psychologists;

        // Apply filters - search takes priority over specialty
        if (search != null && !search.trim().isEmpty()) {
            psychologists = psychologistService.findByNameContaining(search.trim());
        } else if (specialty != null && !specialty.trim().isEmpty()) {
            psychologists = psychologistService.findBySpecialty(specialty.trim());
        } else {
            psychologists = psychologistService.listAll();
        }

        // Get all specialties for the filter dropdown
        List<String> specialties = psychologistService.findAllSpecialties();

        model.addAttribute("psychologists", psychologists);
        model.addAttribute("specialties", specialties);
        model.addAttribute("selectedSpecialty", specialty);
        model.addAttribute("searchName", search);

        return "psychologists";
    }

    @GetMapping("/psychologists/create")
    public String showPsychologistForm(Model model) {
        model.addAttribute("psychologist", new Psychologist());
        return "create-psychologist";
    }

    @GetMapping("/psychologists/{id}/availability/view")
    public String viewPsychologistAvailability(@PathVariable Long id, Model model, Authentication authentication) {

        try {
            Optional<Psychologist> psychologistOpt = psychologistService.findById(id);

            if (psychologistOpt.isEmpty()) {
                return "error";
            }

            Psychologist psychologist = psychologistOpt.get();

            List<AvailabilitySlot> slots = availabilitySlotService.findByPsychologist(id);

            model.addAttribute("psychologist", psychologist);
            model.addAttribute("slots", slots);

            // Check if user is a patient (public view) or psychologist
            if (authentication != null) {
                String email = authentication.getName();
                Patient patient = patientService.findByEmail(email);
                if (patient != null) {
                    model.addAttribute("isPublic", true);
                } else {
                    model.addAttribute("isPublic", false);
                }
            } else {
                model.addAttribute("isPublic", true);
            }

            return "psychologist-availability-view";

        } catch (Exception e) {
            System.out.println("DEBUG: Error in viewPsychologistAvailability: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/psychologists/availability/manage")
    public String manageMyAvailability(Authentication authentication) {
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);
        return "redirect:/psychologists/" + psychologist.getId() + "/availability";
    }

    @GetMapping("/test-view-availability/1")
    public String testViewAvailability() {
        return "redirect:/psychologists/1/availability/view";
    }

    @GetMapping("/psychologist/profile/edit")
    public String showPsychologistEditProfile(Model model, Authentication authentication) {
        if (!model.containsAttribute("psychologist")) {
            String email = authentication.getName();
            Psychologist psychologist = psychologistService.findByEmail(email);

            if (psychologist == null) {
                return "redirect:/login";
            }

            model.addAttribute("psychologist", psychologist);
        }

        return "psychologist-edit-profile";
    }

    //Register new account
    @GetMapping("/register")
    public String chooseRegisterType() {
        return "register";
    }

    @GetMapping("/dashboard-redirect")
    public String dashboardRedirect(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PSYCHOLOGIST"))) {
            return "redirect:/psychologist/dashboard";
        } else if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))) {
            return "redirect:/patient/dashboard";
        }
        return "redirect:/";
    }

    @GetMapping("/test-role")
    public String testRole(Authentication authentication) {
        if (authentication == null) {
            return "Not authenticated";
        }
        return "User: " + authentication.getName()
                + ", Authorities: " + authentication.getAuthorities();
    }

    //Login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    //Resource library
    @GetMapping("/resources")
    public String viewTherapeuticLibrary(Authentication authentication, Model model) {
        List<Resource> allResources = resourceService.listAll();

        // Separate resources by type
        List<Resource> videos = allResources.stream()
                .filter(r -> "video".equalsIgnoreCase(r.getType()))
                .collect(Collectors.toList());

        List<Resource> ebooks = allResources.stream()
                .filter(r -> "ebook".equalsIgnoreCase(r.getType()))
                .collect(Collectors.toList());

        List<Resource> articles = allResources.stream()
                .filter(r -> "article".equalsIgnoreCase(r.getType()))
                .collect(Collectors.toList());

        List<Resource> exercises = allResources.stream()
                .filter(r -> "exercise".equalsIgnoreCase(r.getType()))
                .collect(Collectors.toList());

        // Extract unique subjects for the filter
        List<String> subjects = allResources.stream()
                .map(Resource::getSubject)
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("videos", videos);
        model.addAttribute("ebooks", ebooks);
        model.addAttribute("articles", articles);
        model.addAttribute("exercises", exercises);
        model.addAttribute("subjects", subjects);

        // Add user information to the model based on role
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();

            // Try to find as patient first
            Patient patient = patientService.findByEmail(email);
            if (patient != null) {
                model.addAttribute("userType", "patient");
                model.addAttribute("user", patient);
            } else {
                // Try as psychologist
                Psychologist psychologist = psychologistService.findByEmail(email);
                if (psychologist != null) {
                    model.addAttribute("userType", "psychologist");
                    model.addAttribute("user", psychologist);
                }
            }
        }

        return "therapeutic-resources";
    }   

    @GetMapping("/resources/{type}/{resourceId}")
    public String resourceDetail(@PathVariable String type, @PathVariable Long resourceId, Model model) {
        Resource resource = resourceService.findById(resourceId);
        if (resource == null || !type.equals(resource.getType())) {
            return "error";
        }
        model.addAttribute("resource", resource);
        model.addAttribute("resourceType", capitalize(type));
        return "resource-detail";
    }

    // Resource Management for Psychologists
    @GetMapping("/psychologist/resources")
    public String psychologistResources(Authentication authentication, Model model) {
        System.out.println("DEBUG: Loading psychologist resources page");

        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            System.out.println("DEBUG: Psychologist not found for resources page");
            return "redirect:/login";
        }

        List<Resource> resources = resourceService.listAll();
        System.out.println("DEBUG: Found " + resources.size() + " resources");

        model.addAttribute("resources", resources);
        model.addAttribute("psychologist", psychologist);

        return "psychologist-resources";
    }

    @GetMapping("/psychologist/resources/videos")
    public String manageVideos(Authentication authentication, Model model) {
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            return "redirect:/login";
        }

        List<Resource> videos = resourceService.getByType("video");
        model.addAttribute("resources", videos);
        model.addAttribute("resourceType", "Videos");
        model.addAttribute("psychologist", psychologist);
        model.addAttribute("newResource", new Resource());

        return "psychologist-resource-type";
    }

// Do the same for ebooks, articles, exercises...
    @GetMapping("/psychologist/resources/ebooks")
    public String manageEbooks(Authentication authentication, Model model) {
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            return "redirect:/login";
        }

        List<Resource> ebooks = resourceService.getByType("ebook");
        model.addAttribute("resources", ebooks);
        model.addAttribute("resourceType", "E-books");
        model.addAttribute("psychologist", psychologist);
        model.addAttribute("newResource", new Resource());

        return "psychologist-resource-type";
    }

    @GetMapping("/psychologist/resources/articles")
    public String manageArticles(Authentication authentication, Model model) {
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            return "redirect:/login";
        }

        List<Resource> articles = resourceService.getByType("article");
        model.addAttribute("resources", articles);
        model.addAttribute("resourceType", "Articles");
        model.addAttribute("psychologist", psychologist);
        model.addAttribute("newResource", new Resource());

        return "psychologist-resource-type";
    }

    @GetMapping("/psychologist/resources/exercises")
    public String manageExercises(Authentication authentication, Model model) {
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            return "redirect:/login";
        }

        List<Resource> exercises = resourceService.getByType("exercise");
        model.addAttribute("resources", exercises);
        model.addAttribute("resourceType", "Exercises");
        model.addAttribute("psychologist", psychologist);
        model.addAttribute("newResource", new Resource());

        return "psychologist-resource-type";
    }

    @GetMapping("/test-add-availability/1")
    public String addTestAvailabilityForPsychologist1() {
        Long psychologistId = 1L;

        Optional<Psychologist> psychologistOpt = psychologistService.findById(psychologistId);
        if (psychologistOpt.isEmpty()) {
            return "error";
        }

        Psychologist psychologist = psychologistOpt.get();

        // Clear existing slots first
        availabilitySlotService.deleteByPsychologistId(psychologistId);

        // Add test availability slots
        AvailabilitySlot slot1 = new AvailabilitySlot(psychologist, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0));
        AvailabilitySlot slot2 = new AvailabilitySlot(psychologist, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(17, 0));
        AvailabilitySlot slot3 = new AvailabilitySlot(psychologist, DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(15, 0));
        AvailabilitySlot slot4 = new AvailabilitySlot(psychologist, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(11, 0));
        AvailabilitySlot slot5 = new AvailabilitySlot(psychologist, DayOfWeek.THURSDAY, LocalTime.of(13, 0), LocalTime.of(16, 0));

        availabilitySlotService.save(slot1);
        availabilitySlotService.save(slot2);
        availabilitySlotService.save(slot3);
        availabilitySlotService.save(slot4);
        availabilitySlotService.save(slot5);

        return "redirect:/psychologists/" + psychologistId + "/availability/view";
    }

    @GetMapping("/psychologist/dashboard")
    public String psychologistDashboard(Authentication authentication, Model model) {

        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            return "redirect:/login";
        }

        // Get upcoming appointments
        List<Appointment> upcoming = appointmentService.listByPsychologistId(psychologist.getId());

        // Get availability
        List<AvailabilitySlot> slots = availabilitySlotService.findByPsychologist(psychologist.getId());

        // Get patient count
        List<Patient> patients = patientService.findByPsychologist(psychologist);
        int patientCount = patients.size();

        model.addAttribute("psychologist", psychologist);
        model.addAttribute("appointments", upcoming);
        model.addAttribute("slots", slots);
        model.addAttribute("patientCount", patientCount);

        return "psychologist-dashboard";
    }

    @GetMapping("/psychologist/appointments")
    public String psychologistAppointments(Authentication authentication, Model model) {
        // Get the logged-in psychologist
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            return "redirect:/login";
        }

        // Get only the psychologist's own appointments
        List<Appointment> appointments = appointmentService.listByPsychologistId(psychologist.getId());
        model.addAttribute("appointments", appointments);
        model.addAttribute("psychologist", psychologist);

        return "psychologist-appointments";
    }

    @GetMapping("/psychologists/{id}/availability")
    public String showAvailability(@PathVariable Long id, Model model, Authentication authentication) {

        // Get the logged-in psychologist
        String email = authentication.getName();
        Psychologist loggedInPsychologist = psychologistService.findByEmail(email);

        // Security check: ensure psychologist can only access their own availability
        if (!loggedInPsychologist.getId().equals(id)) {
            throw new RuntimeException("You can only manage your own availability");
        }

        Psychologist psychologist = psychologistService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Psychologist not found: " + id));

        // Load existing availability slots
        List<AvailabilitySlot> slots = availabilitySlotService.findByPsychologist(id);

        // Load psychologist's patients for appointment scheduling
        List<Patient> patients = patientService.findByPsychologist(psychologist);

        // Load psychologist's appointments to display on grid
        List<Appointment> appointments = appointmentService.listByPsychologistId(id);

        model.addAttribute("psychologist", psychologist);
        model.addAttribute("slots", slots);
        model.addAttribute("patients", patients);
        model.addAttribute("appointments", appointments);

        return "psychologist-availability";
    }

    @GetMapping("/test-add-patients")
    public String addTestPatients(Authentication authentication, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            redirectAttributes.addFlashAttribute("error", "Psychologist not found");
            return "redirect:/patients";
        }

        // Check if patients already exist for this psychologist
        List<Patient> existingPatients = patientService.findByPsychologist(psychologist);
        if (!existingPatients.isEmpty()) {
            redirectAttributes.addFlashAttribute("info", "You already have " + existingPatients.size() + " patients");
            return "redirect:/patients";
        }

        try {
            // Create test patient 1
            Patient patient1 = new Patient();
            patient1.setfName("John");
            patient1.setlName("Doe");
            patient1.setEmail("john.doe@example.com");
            patient1.setPassword(passwordEncoder.encode("password123"));
            patient1.setPhone("+3538683016106");
            patient1.setCountry("Ireland");
            patient1.setPsychologist(psychologist);
            patientService.save(patient1);

            // Create test patient 2
            Patient patient2 = new Patient();
            patient2.setfName("Maria");
            patient2.setlName("Santos");
            patient2.setEmail("maria.santos@example.com");
            patient2.setPassword(passwordEncoder.encode("password123"));
            patient2.setPhone("+353879876543");
            patient2.setCountry("Brazil");
            patient2.setPsychologist(psychologist);
            patientService.save(patient2);

            redirectAttributes.addFlashAttribute("success", "Added 2 test patients to your practice");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding test patients: " + e.getMessage());
        }

        return "redirect:/patients";
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(@RequestParam Long psychologistId,
            @RequestParam String selectedDay,
            @RequestParam String selectedTime,
            @RequestParam(required = false) String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String email = authentication.getName();
            Patient patient = patientService.findByEmail(email);
            Psychologist psychologist = psychologistService.findById(psychologistId)
                    .orElseThrow(() -> new IllegalArgumentException("Psychologist not found"));

            // Convert day and time to actual datetime
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(selectedDay.toUpperCase());
            LocalTime startTime = LocalTime.parse(selectedTime);

            // Calculate the next occurrence of this day
            LocalDate appointmentDate = calculateNextDate(dayOfWeek);
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, startTime);

            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setPsychologist(psychologist);
            appointment.setDateHour(appointmentDateTime);
            appointment.setEndTime(appointmentDateTime.plusHours(1)); // 1-hour sessions
            appointment.setNotes(notes);
            appointment.setPaid(false);
            appointment.setDurationMinutes(60);

            appointmentService.save(appointment);

            redirectAttributes.addFlashAttribute("success",
                    "Appointment booked successfully for " + appointmentDate + " at " + startTime);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error booking appointment: " + e.getMessage());
            e.printStackTrace(); // Add this for debugging
        }

        return "redirect:/patient/dashboard";
    }

    @PostMapping("/psychologist/appointments/create")
    public String createAppointmentByPsychologist(
            @RequestParam Long patientId,
            @RequestParam String appointmentDate,
            @RequestParam String appointmentTime,
            @RequestParam(defaultValue = "60") Integer durationMinutes,
            @RequestParam(required = false) String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            System.out.println("=== CREATING APPOINTMENT ===");
            System.out.println("Patient ID: " + patientId);
            System.out.println("Date: " + appointmentDate);
            System.out.println("Time: " + appointmentTime);

            // Get the logged-in psychologist
            String email = authentication.getName();
            Psychologist psychologist = psychologistService.findByEmail(email);

            if (psychologist == null) {
                redirectAttributes.addFlashAttribute("error", "Psychologist not found");
                return "redirect:/login";
            }

            // Get the patient
            Patient patient = patientService.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

            // Security check: ensure the patient belongs to this psychologist
            if (patient.getPsychologist() == null
                    || !patient.getPsychologist().getId().equals(psychologist.getId())) {
                redirectAttributes.addFlashAttribute("error",
                        "You can only schedule appointments for your own patients");
                return "redirect:/psychologists/" + psychologist.getId() + "/availability";
            }

            // Parse date and time
            LocalDate date = LocalDate.parse(appointmentDate);
            LocalTime time = LocalTime.parse(appointmentTime);
            LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);

            // Check if appointment is in the past
            if (appointmentDateTime.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error",
                        "Cannot schedule appointments in the past");
                return "redirect:/psychologists/" + psychologist.getId() + "/availability";
            }

            // Create the appointment
            Appointment appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setPsychologist(psychologist);
            appointment.setDateHour(appointmentDateTime);
            appointment.setDurationMinutes(durationMinutes);
            appointment.setEndTime(appointmentDateTime.plusMinutes(durationMinutes));
            appointment.setNotes(notes);
            appointment.setPaid(false);

            appointmentService.save(appointment);

            System.out.println("Appointment created successfully");
            redirectAttributes.addFlashAttribute("success",
                    "Appointment scheduled successfully for " + patient.getfName() + " " + patient.getlName()
                    + " on " + date.toString() + " at " + time.toString());

        } catch (Exception e) {
            System.out.println("ERROR creating appointment: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error scheduling appointment: " + e.getMessage());
        }

        // Redirect back to availability page
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);
        return "redirect:/psychologists/" + psychologist.getId() + "/availability";
    }

    private LocalDate calculateNextDate(DayOfWeek dayOfWeek) {
        LocalDate today = LocalDate.now();
        int daysToAdd = (dayOfWeek.getValue() - today.getDayOfWeek().getValue() + 7) % 7;
        daysToAdd = daysToAdd == 0 ? 7 : daysToAdd; // Next week if same day
        return today.plusDays(daysToAdd);
    }

    @PostMapping("/patients/create")
    public String savePatient(
            @Valid @ModelAttribute("patient") Patient patient,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct the errors below.");
            return "create-patient";
        }

        if (patientService.emailExists(patient.getEmail())) {
            model.addAttribute("error", "This email is already registered.");
            return "create-patient";
        }
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        patientService.save(patient);
        redirectAttributes.addFlashAttribute("success", "Account created successfully!");
        return "redirect:/login";
    }

    @PostMapping("/patient/profile/edit")
    public String updateProfile(
            @ModelAttribute("patient") Patient updated,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);

        if (patient == null) {
            redirectAttributes.addFlashAttribute("error", "Error: Account not found.");
            return "redirect:/login";
        }

        // Update allowed editable fields
        patient.setfName(updated.getfName());
        patient.setlName(updated.getlName());
        patient.setPhone(updated.getPhone());
        patient.setCountry(updated.getCountry());
        patient.setNotes(updated.getNotes());

        // Optional password update - only if not blank
        if (updated.getPassword() != null && !updated.getPassword().trim().isEmpty()) {
            if (updated.getPassword().trim().length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters");
                return "redirect:/patient/profile/edit";
            }
            patient.setPassword(passwordEncoder.encode(updated.getPassword().trim()));
        }

        patientService.save(patient);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/patient/dashboard";  // ← Changed to dashboard for better UX
    }

    @PostMapping("/patient/payment-method/save")
    public String savePaymentMethod(@RequestParam String paymentMethodId, Authentication authentication, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Patient patient = patientService.findByEmail(email);

        // In a real application, you would attach the paymentMethodId to the patient's
        // Stripe Customer ID using the Stripe SDK. For now, we will just simulate success and update the patient's notes with the last saved card ID.
        String successMessage = "Payment method saved successfully! Card ID: " + paymentMethodId;

        // Optional: Update patient notes to reflect the saved card (for demonstration)
        patient.setNotes("Last saved Payment Method ID: " + paymentMethodId);
        patientService.save(patient);

        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/patient/payment-method";
    }

    @PostMapping("/psychologists/create")
    public String registerPsychologist(
            @Valid @ModelAttribute("psychologist") Psychologist psychologist,
            BindingResult bindingResult,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        // 1. Field validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please correct the errors below.");
            return "create-psychologist";
        }

        // 2. Email already exists
        if (psychologistService.emailExists(psychologist.getEmail())) {
            model.addAttribute("error", "This email is already registered.");
            return "create-psychologist";
        }

        // 3. Passwords must match
        if (!psychologist.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "create-psychologist";
        }

        // 4. Encode password
        psychologist.setPassword(passwordEncoder.encode(psychologist.getPassword()));

        // 5. Save
        Psychologist saved = psychologistService.save(psychologist);

        //6. Redirect
        redirectAttributes.addFlashAttribute("Success", "Registration successful! Please login to access your account.");
        return "redirect:/login";
    }

    @PostMapping("/psychologists/{psychologistId}/availability/{slotId}/delete")
    public String deleteAvailabilitySlot(@PathVariable Long psychologistId,
            @PathVariable Long slotId) {
        availabilitySlotService.deleteById(slotId);
        return "redirect:/psychologists/" + psychologistId + "/availability";
    }

    @PostMapping("/psychologists/{id}/availability")
    public String addAvailabilitySlot(
            @PathVariable Long id,
            @RequestParam String dayOfWeek,
            @RequestParam String startTime,
            @RequestParam String endTime,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        // Security check
        String email = authentication.getName();
        Psychologist loggedInPsychologist = psychologistService.findByEmail(email);
        if (!loggedInPsychologist.getId().equals(id)) {
            throw new RuntimeException("You can only manage your own availability");
        }

        Psychologist psychologist = psychologistService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Psychologist not found: " + id));

        try {
            // Convert day of week string to enum
            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());

            // Parse times
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            // Validate time range
            if (end.isBefore(start) || end.equals(start)) {
                redirectAttributes.addFlashAttribute("error", "End time must be after start time.");
                return "redirect:/psychologists/" + id + "/availability";
            }

            // Create and save the slot
            AvailabilitySlot slot = new AvailabilitySlot(psychologist, day, start, end);
            availabilitySlotService.save(slot);

            redirectAttributes.addFlashAttribute("success", "Availability slot added successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding slot: " + e.getMessage());
        }

        return "redirect:/psychologists/" + id + "/availability";
    }

    @PostMapping("/psychologists/{id}/availability/save")
    public String saveAvailability(
            @PathVariable Long id,
            @RequestParam(value = "slots", required = false) List<String> slots,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        // Security check
        String email = authentication.getName();
        Psychologist loggedInPsychologist = psychologistService.findByEmail(email);
        if (!loggedInPsychologist.getId().equals(id)) {
            throw new RuntimeException("You can only manage your own availability");
        }

        Psychologist psychologist = psychologistService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Psychologist not found: " + id));

        try {
            // Delete existing slots for this psychologist
            availabilitySlotService.deleteByPsychologistId(id);

            int savedCount = 0;

            // Process the grid slots if any were selected
            if (slots != null && !slots.isEmpty()) {
                for (String slot : slots) {
                    String[] parts = slot.split("\\|");
                    if (parts.length == 2) {
                        String dayOfWeek = parts[0];
                        String time = parts[1];

                        // Parse day and time
                        DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
                        LocalTime startTime = LocalTime.parse(time);
                        LocalTime endTime = startTime.plusHours(1); // Assuming 1-hour slots

                        // Create and save the slot
                        AvailabilitySlot availabilitySlot = new AvailabilitySlot(psychologist, day, startTime, endTime);
                        availabilitySlotService.save(availabilitySlot);
                        savedCount++;
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success",
                    "Availability saved successfully! " + savedCount + " time slot(s) configured.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error saving availability: " + e.getMessage());
        }

        return "redirect:/psychologist/dashboard";
    }

    @PostMapping("/appointments/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String email = authentication.getName();

            // Find the appointment
            Optional<Appointment> appointmentOpt = appointmentService.findById(id);
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Appointment not found"
                ));
            }

            Appointment appointment = appointmentOpt.get();

            // Security check: ensure psychologist owns this appointment
            if (!appointment.getPsychologist().getEmail().equals(email)) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "You can only cancel your own appointments"
                ));
            }

            // Cancel the appointment
            appointmentService.deleteById(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment cancelled successfully"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error cancelling appointment: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/psychologist/appointments/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelAppointmentByPsychologist(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            String email = authentication.getName();

            // Find the appointment
            Optional<Appointment> appointmentOpt = appointmentService.findById(id);
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Appointment not found"
                ));
            }

            Appointment appointment = appointmentOpt.get();

            // Security check: ensure psychologist owns this appointment
            if (!appointment.getPsychologist().getEmail().equals(email)) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "You can only cancel your own appointments"
                ));
            }

            // Cancel the appointment
            appointmentService.deleteById(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment cancelled successfully"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error cancelling appointment: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/psychologist/patients/create")
    @ResponseBody
    public ResponseEntity<?> createPatientForPsychologist(
            @RequestParam String fName,
            @RequestParam String lName,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String emergencyContact,
            @RequestParam(required = false) String notes,
            Authentication authentication) {

        try {
            System.out.println("=== CREATE PATIENT REQUEST ===");
            System.out.println("Psychologist: " + authentication.getName());
            System.out.println("Patient: " + fName + " " + lName + " (" + email + ")");

            String psychologistEmail = authentication.getName();
            Psychologist psychologist = psychologistService.findByEmail(psychologistEmail);

            if (psychologist == null) {
                System.out.println("ERROR: Psychologist not found for email: " + psychologistEmail);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Psychologist not found. Please log in again."
                ));
            }

            // Check if patient with email already exists
            if (patientService.emailExists(email)) {
                System.out.println("ERROR: Patient email already exists: " + email);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "A patient with this email already exists."
                ));
            }

            // Create new patient
            Patient patient = new Patient();
            patient.setfName(fName);
            patient.setlName(lName);
            patient.setEmail(email);
            patient.setPhone(phone);
            patient.setCountry(country);
            patient.setNotes(notes);
            patient.setPsychologist(psychologist);

            // Set a temporary password
            String tempPassword = "Welcome123";
            patient.setPassword(passwordEncoder.encode(tempPassword));

            Patient savedPatient = patientService.save(patient);

            System.out.println("SUCCESS: Patient created with ID: " + savedPatient.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Patient created successfully");
            response.put("patientId", savedPatient.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("ERROR: Exception creating patient: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error creating patient: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/psychologist/profile/edit")
    public String updatePsychologistProfile(
            @ModelAttribute("psychologist") Psychologist updated,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            redirectAttributes.addFlashAttribute("error", "Error: Account not found.");
            return "redirect:/login";
        }

        // Update allowed editable fields
        psychologist.setfName(updated.getfName());
        psychologist.setlName(updated.getlName());
        psychologist.setPhoneNumber(updated.getPhoneNumber());
        psychologist.setSpecialty(updated.getSpecialty());

        // Optional password update - only update if not blank
        if (updated.getPassword() != null && !updated.getPassword().trim().isEmpty()) {
            if (updated.getPassword().trim().length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters");
                return "redirect:/psychologist/profile/edit";
            }
            psychologist.setPassword(passwordEncoder.encode(updated.getPassword().trim()));
        }
        // If password is blank, we simply don't update it (keep existing password)

        // Save the psychologist (whether password was updated or not)
        psychologistService.save(psychologist);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/psychologist/dashboard";
    }

    @PostMapping("/psychologist/resources/create")
    public String createResource(@ModelAttribute Resource resource,
            @RequestParam String resourceType,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            return "redirect:/login";
        }

        try {
            // Set resource type based on the management page
            resource.setType(resourceType.toLowerCase());
            resourceService.save(resource);

            redirectAttributes.addFlashAttribute("success",
                    resourceType + " resource created successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error creating resource: " + e.getMessage());
        }

        return "redirect:/psychologist/resources/" + resourceType.toLowerCase() + "s";
    }

    @PostMapping("/psychologist/resources/{id}/delete")
    public String deleteResource(@PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Psychologist psychologist = psychologistService.findByEmail(email);

        if (psychologist == null) {
            return "redirect:/login";
        }

        try {
            resourceService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Resource deleted successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error deleting resource: " + e.getMessage());
        }

        return "redirect:/psychologist/resources";
    }

    @ModelAttribute
    public void addUserToModel(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();

            boolean isPatient = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_PATIENT"));

            boolean isPsychologist = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_PSYCHOLOGIST"));

            if (isPatient) {
                Patient patient = patientService.findByEmail(email);
                if (patient != null) {
                    model.addAttribute("patient", patient);
                }
            } else if (isPsychologist) {
                Psychologist psychologist = psychologistService.findByEmail(email);
                if (psychologist != null) {
                    model.addAttribute("psychologist", psychologist);
                }
            }
        }
    }
}
