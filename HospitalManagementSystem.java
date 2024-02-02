package secure;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import secure.MyLogger;


public class HospitalManagementSystem {
	

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("1-Login");
		System.out.println("2-Exit");

		int Choice = sc.nextInt();
		try {
			switch (Choice) {
			case 1:
				adminLogin();
				break;
			case 2:
				break;
			default:
				System.out.println("Unknown user type. Information not saved.");
			}

			userLogin();
		} finally {
			sc.close();
		}
	}

	private static void adminLogin() {
		String adminUsername, adminPassword;
		int count = 0;

		while (count <= 3) {
			Scanner sc = new Scanner(System.in);

			System.out.println("Username: ");
			adminUsername = sc.nextLine();

			System.out.println("Password: ");
			adminPassword = sc.nextLine();

			String hashedPassword = getHash(adminPassword);

			if (isValidAdminCredentials(adminUsername, hashedPassword)) {
				System.out.println("Admin login successful!");
				MyLogger.writeToLog(hashedPassword);
				MyLogger.writeToLog("The user " + adminUsername + " has Loged in successfuly");
				adminReg();
				return; // Exit the method after successful login
			} else {
				System.out.println("Incorrect admin username or password. Try again.");
				count++;
			}
			if (count == 3) {
				System.out.println("Too many failed login attempts. Exiting.");
				return;
			}
		}
	}

	private static boolean isValidAdminCredentials(String adminUsername, String hashedPassword) {
		BufferedReader br = null;

		try {
			br = new BufferedReader(
					new FileReader("C:\\\\Users\\\\hp\\\\eclipse-workspace\\\\secure\\\\adminLogin.txt"));

			String line;
			while ((line = br.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns.length >= 2 && columns[0].equalsIgnoreCase(adminUsername)
						&& columns[1].equals(hashedPassword)) {
					return true; // Valid admin credentials found in the file
				}
			}
		} catch (IOException e) {
			System.err.println("Error while reading admin credentials file");

		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				System.err.println("Error closing the reader");
			}
		}
		return false; // No match found or error occurred
	}

	private static String getHash(String value) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			result = encode(md.digest(value.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			System.err.println("The Algorithm doesn't exist");
		}
		return result;
	}

	private static String encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	private static void adminReg() {

		Scanner Reg = new Scanner(System.in);
		System.out.println("Register new user");

		String newUserType, newUsername, newPassword, newGender;
		int newAge;
		long newPhoneNumber;

		System.out.println("Choose the user type (patient/doctor):");
		newUserType = Reg.nextLine().toLowerCase();

		try {
			System.out.println("Enter the information for the new user");

			System.out.println("Name:");
			newUsername = Reg.nextLine();

			// Password Policy Check
			do {
			    System.out.println("Password (at least one capital letter, one symbol, one number, max length 15):");
			    newPassword = Reg.nextLine();

			    if (checkPolicy(newPassword)) {
			        break;
			    } else {
			        System.out.println("Password does not meet the criteria. Try again.");
			    }
			} while (true);


			System.out.println("Gender:");
			newGender = Reg.nextLine();

			System.out.println("Phonenumber:");
			newPhoneNumber = Reg.nextLong();

			System.out.println("Age:");
			newAge = Reg.nextInt();
			Reg.nextLine();

			switch (newUserType) {
			case "patient":
				writeToFile("C:\\\\Users\\\\hp\\\\eclipse-workspace\\\\secure\\\\Patient.txt",
						newUsername, getHash(newPassword), newPhoneNumber, newGender, newAge);
				break;
			case "doctor":
				writeToFile("C:\\\\Users\\\\hp\\\\eclipse-workspace\\\\secure\\\\Doctor.txt", newUsername,
						getHash(newPassword), newPhoneNumber, newGender, newAge);
				break;
			default:
				System.out.println("Unknown user type. Information not saved.");
			}
		} catch (InputMismatchException e) {
			System.err.println("Invalid input");
		}
	}

	private static boolean checkPolicy(String password) {
	    // Allow any character and require at least one capital letter, one digit, and one special character
	    String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%?&])[A-Za-z\\d@$!%?&]{1,15}$";
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(password);
	    return matcher.matches();
	}



	

	private static void userLogin() {
		String username, password;
		Scanner sc = new Scanner(System.in);
		int count = 0;

		while (count <= 3) {

			System.out.println("Choose the user type (doctor/patient):");
			String userType = sc.nextLine().toLowerCase();

			System.out.println("Username: ");
			username = sc.nextLine();

			System.out.println("Password: ");
			password = sc.nextLine();

			String hashedPassword = getHash(password);

			switch (userType) {
			case "doctor":
				if (isValidDoctorCredentials(username, hashedPassword)) {
					System.out.println("Doctor login successful!");
					MyLogger.writeToLog("The user " + username + " has Loged in successfuly");
					Doctor(username);
					return; // Exit the method after successful login
				} else {
					System.out.println("Incorrect doctor username or password. Try again.");
				}
				break;
			case "patient":
				if (isValidPatientCredentials(username, hashedPassword)) {
					System.out.println("Patient login successful!");
					MyLogger.writeToLog("The user " + username + " has Loged in successfuly");
					Patient(username);
					return; // Exit the method after successful login
				} else {
					System.out.println("Incorrect patient username or password. Try again.");
				}
				break;
			default:
				System.out.println("Invalid user type. Try again.");
				break;
			}
			count++;
		}
		sc.close();
		System.out.println("Too many failed login attempts. Exiting.");

	}

	private static void Doctor(String username) {
		Scanner sc = new Scanner(System.in);
		System.out.println("1-View Doctor information");
		System.out.println("2-enter medical information of patient");
		int choice = sc.nextInt();
		switch (choice) {
		case 1:
			viewInformationDoctor(username);
			break;
		case 2:
			enterMedInfo(username);
			break;

		default:
			System.out.println("invalid input");
		}
	}

	private static void Patient(String username) {
		Scanner sc = new Scanner(System.in);
		System.out.println("1-View Patient information");
		System.out.println("2-View medical information");
		int choice = sc.nextInt();
		switch (choice) {
		case 1:
			viewInformationPatient(username);
			break;
		case 2:
			viewMedRecord(username);
			break;

		default:
			System.out.println("invalid input");
		}
	}

	private static void viewInformationDoctor(String Username) {
		BufferedReader doc = null;
		try {
			doc = new BufferedReader(new FileReader("C:\\\\Users\\\\hp\\\\eclipse-workspace\\\\secure\\\\Doctor.txt"));
			String line;
			while ((line = doc.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns.length >= 5 && columns[0].equals(Username)) {
					String doctorName = columns[0];
					int phoneNumber = Integer.parseInt(columns[2]);
					String gender = columns[3];
					int age = Integer.parseInt(columns[4]);

					System.out.println("Doctor Information:");
					System.out.println("Name: " + doctorName);
					System.out.println("Phone Number: " + phoneNumber);
					System.out.println("Gender: " + gender);
					System.out.println("Age: " + age);

					break; // Exit the loop after finding the doctor's information
				}
			}
		} catch (IOException e) {
			System.out.println("error read the file");
		} finally {
			try {
				if (doc != null) {
					doc.close();
				}
			} catch (IOException e) {
				System.err.println("Error closing");
			}
		}
	}

	private static void viewInformationPatient(String Username) {
		BufferedReader pat = null;

		try {
			pat = new BufferedReader(new FileReader("C:\\\\Users\\\\hp\\\\eclipse-workspace\\\\secure\\\\Patient.txt"));
			String line;
			while ((line = pat.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns.length >= 5 && columns[0].equals(Username)) {
					String patientName = columns[0];
					int phoneNumber = Integer.parseInt(columns[2]);
					String gender = columns[3];
					int age = Integer.parseInt(columns[4]);

					System.out.println("Patient Information:");
					System.out.println("Name: " + patientName);
					System.out.println("Phone Number: " + phoneNumber);
					System.out.println("Gender: " + gender);
					System.out.println("Age: " + age);

					break; // Exit the loop after finding the patient's information
				}
			}
		} catch (IOException e) {
			System.out.println("error read the file");
		} finally {
			try {
				if (pat != null) {
					pat.close();
				}
			} catch (IOException e) {
				System.err.println("Error closing");
			}
		}
	}

	private static boolean isValidPatientCredentials(String username, String hashedPassword) {
		BufferedReader br = null;

		try {
			br = new BufferedReader(
					new FileReader("C:\\\\Users\\\\hp\\\\eclipse-workspace\\\\secure\\\\Patient.txt"));

			String line;
			while ((line = br.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns.length >= 2 && columns[0].equalsIgnoreCase(username) && columns[1].equals(hashedPassword)) {
					return true; // Valid admin credentials found in the file
				}
			}
		} catch (IOException e) {
			System.err.println("Error while reading Patient credentials file");

		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				System.err.println("Error closing the reader");
			}
		}
		return false; // No match found or error occurred
	}

	private static boolean isValidDoctorCredentials(String username, String hashedPassword) {
		BufferedReader br = null;

		try {
			br = new BufferedReader(
					new FileReader("C:\\\\Users\\\\hp\\\\eclipse-workspace\\\\secure\\\\Doctor.txt"));

			String line;
			while ((line = br.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns.length >= 2 && columns[0].equalsIgnoreCase(username) && columns[1].equals(hashedPassword)) {
					return true; // Valid admin credentials found in the file
				}
			}
		} catch (IOException e) {
			System.err.println("Error while reading Doctor credentials file");

		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				System.err.println("Error closing the reader");
			}
		}
		return false; // No match found or error occurred
	}

	private static void writeToFile(String fileName, String username, String password, Long phoneNumber, String gender,
			int age) {
		BufferedReader br = null;
		BufferedWriter writer = null;

		try {
			br = new BufferedReader(new FileReader(fileName));

			String line;
			while ((line = br.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns.length >= 1 && columns[0].equalsIgnoreCase(username)) {
					System.out.println("Username already exists. User not added.");

					return;
				}
				if (columns.length >= 3 && columns[2].equals(String.valueOf(phoneNumber))) {
					System.out.println("Phone number already exists. User not added.");
				}
			}

			writer = new BufferedWriter(new FileWriter(fileName, true));

			writer.write(username + "," + password + "," + phoneNumber + "," + gender + "," + age);
			writer.newLine();
			System.out.println("User added successfully.");

		} catch (IOException | NumberFormatException e) {
			System.err.println("Error");
			System.err.println("Error writing to the file: ");
			System.err.println("Error While writing the file Or Wrong Number format");
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				System.err.println("Error Closing the reader or writer ");
			}
		}
	}

	private static void enterMedInfo(String doctorName) {
		Scanner input = new Scanner(System.in);

		System.out.println("Enter patient's name: ");
		String patientName = input.nextLine();

		System.out.println("Enter medical situation: ");
		String medicalSituation = input.nextLine();

		System.out.println("Enter medical treatment: ");
		String medicalTreatment = input.nextLine();

		// Save the medical information to a file
		writeMedicalInfoToFile("C:\\\\Users\\\\hp\\\\eclipse-workspace\\\\secure\\\\MedicalInformation.txt",
				doctorName, patientName, medicalSituation, medicalTreatment);
		System.out.println("Successful Saved");
	}

	private static void writeMedicalInfoToFile(String fileName, String doctorName, String patientName,
			String medicalSituation, String medicalTreatment) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
			// Append the new medical information to the file
			writer.write(doctorName + "," + patientName + "," + medicalSituation + "," + medicalTreatment);
			writer.newLine(); // Move to the next line for the next entry
		} catch (IOException e) {
			System.err.println("Error writing medical information to the file: ");
			MyLogger.writeToLog("Error: writing medical information to the file", e);
		}
	}

	private static void viewMedRecord(String username) {
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(
					"C:\\\\Users\\\\hp\\\\eclipse-workspace\\\\secure\\\\MedicalInformation.txt"));

			String line;
			while ((line = br.readLine()) != null) {
				String[] columns = line.split(",");

				if (columns.length >= 3 && columns[1].equals(username)) {
					String medicalSituation = columns[2];
					String medicalTreatment = columns[3];

					System.out.println("Medical Record:");
					System.out.println("Medical Situation: " + medicalSituation);
					System.out.println("Medical Treatment: " + medicalTreatment);

					// You can add more information as needed
					break; // Exit the loop after finding the medical record
				}
			}
		} catch (IOException e) {
			System.err.println("Error Reading from file");
			MyLogger.writeToLog("Error: Reading from file", e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				System.err.println("Error Closing the reader");
				MyLogger.writeToLog("Error: Closing the reader", e);
			}
		}
	}

}