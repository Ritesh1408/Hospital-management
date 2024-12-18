package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Root";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("Hospital Management System : ");
                System.out.println("1. Add Patient");
                System.out.println("2. view Patients");
                System.out.println("3. view Doctors");
                System.out.println("4. Book Appointments");
                System.out.println("5. Exit");
                System.out.println("Enter Your Choice : ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        //add patient
                        patient.addPatient();
                        System.out.println();
                        break;

                    case 2:
                        //view patient
                        patient.viewPatients();
                        System.out.println();
                        break;

                    case 3:
                        //view doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //Book Appointments
                        bookAppointment(patient,doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("Thank you for Visiting.");
                        return;
                    default:
                        System.out.println("Enter Your valid Choice!!!!");
                        break;
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    // Book Appointments ///
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.print("Enter Patient id : ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor id : ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD)");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailability(doctorId, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowAffected = preparedStatement.executeUpdate();
                    if(rowAffected>0){
                        System.out.println("Appointment Booked!!");
                    } else{
                        System.out.println("Failed to Book Appointment");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else {
                System.out.println("Doctor not available on this date!!!");
            }

        } else {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }



    }

    //check availability ///
    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()){
                    int count = resultSet.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
