import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;
    private HashMap<LocalDate, Integer> demeritPoints = new HashMap<>(); // A variable that holds the demerit points with the offense day
    private boolean isSuspended;

    public Person(String personID, String firstName, String lastName, String address, String birthDate) {
        this.personID = personID;
        this.address = address;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    private boolean checkDate(String date) {
        String[] dateParts = date.split("-");
        if (dateParts.length != 3) return false; //make sure format is correct

        try {
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            if (month < 1 || month > 12) {
                return false; //make sure it's a valid month
            }

            if (day < 1 || day > 31) {
                return false; //make sure it's a valid day
            }

            if (year < 1 || year > 2025) {
                return false; //make sure it's a valid year
            }
        } catch (NumberFormatException e) {
            return false; //make sure it's a number
        }

        return true;
    }

    private boolean checkID(String id) {
        int len = id.length();
        if (len != 10) {
            return false; //10 chars long
        }

        //first two char is a number between 2-9
        char firstChar = id.charAt(0);
        char secondChar = id.charAt(1);

        if (!Character.isDigit(firstChar) || firstChar < '2' || firstChar > '9') return false;
        if (!Character.isDigit(secondChar)|| secondChar < '2' || secondChar > '9') return false;

        int specialChars = 0;
        for (int i = 1; i < 9; i++) {
            if (!Character.isLetterOrDigit(id.charAt(i))) {
                specialChars++;
            }
        }

        if (specialChars < 2) { //at least 2 special chars between 2-9
            return false;
        }

        if (!Character.isUpperCase(id.charAt(len - 2)) || !Character.isUpperCase(id.charAt(len - 1)))
            return false; //last two chars are uppercase

        return true;
    }

    private boolean checkAddress(String addressToCheck) {
        String[] parts = addressToCheck.split(" \\| ");
        if (parts.length != 5) return false; //make sure format is correct
        if (!parts[3].equals("Victoria")) return false; //make sure state is Victoria
        try {
            Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return false;
        } //make sure street number is a number

        return true;
    }

    private String buildDemeritHistory() {
        StringBuilder demeritHistory = new StringBuilder();
        boolean first = true;

        for (HashMap.Entry<LocalDate, Integer> entry : demeritPoints.entrySet()) {
            if (!first) {
                demeritHistory.append(", ");//if more than one, add comma
            }

            demeritHistory.append(entry.getValue()).append(" on ").append(entry.getKey());//'points' on 'date'
            first = false;
        }

        return demeritHistory.toString();
    }

    public boolean addPerson() {
        //TODO: This method adds information about a person to a TXT file.
        //Condition 1: PersonID should be exactly 10 characters long;
        //the first two characters should be numbers between 2 and 9, there should be at least two special characters between characters 3 and 8,
        //and the last two characters should be upper case letters (A - Z). Example: "56s_d%&fAB"
        //Condition 2: The address of the Person should follow the following format: Street Number | Street | City | State | Country.
        //The State should be only Victoria. Example: 32 | Highland Street | Melbourne | Victoria | Australia.
        //Condition 3: The format of the birth date of the person should follow the following format: DD-MM-YYYY. Example: 15-11-1990
        //Instruction: If the Person's information meets the above conditions and any other conditions you may want to consider,
        //the information should be inserted into a TXT file, and the addPerson function should return true.
        //Otherwise, the information should not be inserted into the TXT file, and the addPerson function should return false.

        //check duplicate
        Path filePath = Paths.get("person.txt");

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (int i = 0; i < lines.size(); ) {
                if (i + 1 >= lines.size()) break;

                String idLine = lines.get(i + 1);
                String id = idLine.replace("ID: ", "");

                if (personID.equals(id)) {//if theres already id in file, return true
                    return true;
                }

                i += 6;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //condition 1
        boolean idCheck = checkID(personID);
        if (!idCheck) {
            return false;
        }

        //condition 2
        boolean addressCheck = checkAddress(address);
        if (!addressCheck) {
            return false;
        }

        //condition 3
        boolean dateCheck = checkDate(birthDate);
        if (!dateCheck) {
            return false;
        }

        String demeritHistory = buildDemeritHistory();

        String content =
                "Name: " + firstName + " " + lastName +
                        "\nID: " + personID +
                        "\nAddress: " + address +
                        "\nBirth Date: " + birthDate +
                        "\nDemerit History: " + demeritHistory + "\n\n";

        try {
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean updatePersonalDetails(String updatedId, String updatedFirstName, String updatedLastName, String updatedAddress, String updatedBirthDate) {
        //TODO: This method allows updating a given person's ID, firstName, lastName, address and birthday in a TXT file.
        //Changing personal details will not affect their demerit points or the suspension status.
        //All relevant conditions discussed for the addPerson function also need to be considered and checked in the updatePerson function.
        //Condition 1: If a person is under 18, their address cannot be changed.
        //Condition 2: If a person's birthday is going to be changed, then no other personal detail (i.e, person's ID, firstName, lastName, address) can be changed.
        //Condition 3: If the first character/digit of a person's ID is an even number, then their ID cannot be changed.
        //Instruction: If the Person's updated information meets the above conditions and any other conditions you may want to consider,
        //the Person's information should be updated in the TXT file with the updated information, and the updatePersonalDetails function should return true.
        //Otherwise, the Person's updated information should not be updated in the TXT file, and the updatePersonalDetails function should return false.

        try {
            Path filePath = Paths.get("person.txt");

            List<String> lines = Files.readAllLines(filePath); //read from file
            ArrayList<String> newLines = new ArrayList<>(lines); //create duplicate to override later

            if (updatedBirthDate != null && (
                            updatedId != null ||
                            updatedFirstName != null ||
                            updatedLastName != null ||
                            updatedAddress != null)) return false;

            boolean birthDateChanged = false;

            for (int i = 0; i < lines.size(); ) {
                if (i + 1 >= lines.size()) break;

                String idLine = lines.get(i + 1);

                String id = idLine.replace("ID: ", "");

                if (id.equals(personID)) {
                    //check birthdate
                    if (updatedBirthDate != null) {
                        //check birthdate format
                        boolean dateCheck = checkDate(updatedBirthDate);
                        if (dateCheck) {
                            birthDateChanged = true;
                            birthDate = updatedBirthDate;
                            newLines.set(i + 3, "\nBirth Date: " + birthDate);

                            i += 6;
                            continue; //skip if birthdate is updated
                        }
                    }

                    if (!birthDateChanged) {
                        //check id
                        if (updatedId != null) {
                            int firstChar = personID.charAt(0);
                            if (firstChar % 2 != 0) { //cant be even number
                                boolean idCheck = checkID(updatedId);
                                if (idCheck) {
                                    personID = updatedId;
                                    newLines.set(i + 1, "\nID: " + personID);
                                }
                            }
                        }

                        //check address
                        if (updatedAddress != null) {
                            int birthYear = Integer.parseInt(birthDate.split("-")[2]);
                            int currentYear = Year.now().getValue();

                            if ((currentYear - birthYear) >= 18) { //older than 18
                                boolean addressCheck = checkAddress(updatedAddress);
                                if (addressCheck) {
                                    address = updatedAddress;
                                    newLines.set(i + 2, "\nAddress: " + address);
                                }
                            } else {
                                return false;
                            }
                        }

                        //check name
                        if (updatedFirstName != null || updatedLastName != null) {
                            if (updatedFirstName != null) {
                                firstName = updatedFirstName;
                            }

                            if (updatedLastName != null) {
                                lastName = updatedLastName;
                            }

                            newLines.set(i, "Name: " + firstName + " " + lastName);
                        }
                    }

                    break;
                }

                i += 6; //including the empty line
            }

            try {
                Files.write(filePath, newLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return false;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

}
