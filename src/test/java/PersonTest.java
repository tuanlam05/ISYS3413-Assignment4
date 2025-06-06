import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {
    private Person person;

    @BeforeEach
    public void setUp() {
        person = new Person(
                "56@#df&*AB",
                "John",
                "Doe",
                "32 | Highland Street | Melbourne | Victoria | Australia",
                "15-11-2000"
        );
    }

    @Test
    public void testAddPerson_ValidData_ShouldReturnTrue() {
        assertTrue(person.addPerson());
    }

    @Test
    public void testAddPerson_InvalidIDLength_ShouldReturnFalse() {
        Person invalidPerson = new Person(
                "56@#df&*ABc",
                "Jane",
                "Smith",
                "32 | Highland Street | Melbourne | Victoria | Australia",
                "15-11-2000"
        );
        assertFalse(invalidPerson.addPerson());
    }

    @Test
    public void testAddPerson_Address_Invalid_Format_ShouldReturnFalse() {
        Person invalidPerson = new Person(
                "56@#df&*AC",
                "Johnny",
                "Smith",
                "32 | Highland Street | Melbourne | Victoria",
                "15-11-2000"
        );
        assertFalse(invalidPerson.addPerson());
    }

    @Test
    public void testAddPerson_Invalid_State_ShouldReturnFalse() {
        Person invalidPerson = new Person(
                "56@#df&*AD",
                "James",
                "Smith",
                "32 | Highland Street | Sydney | New South Wales | Australia",
                "15-11-2000"
        );
        assertFalse(invalidPerson.addPerson());
    }

    @Test
    public void testAddPerson_Invalid_Birthdate_ShouldReturnFalse() {
        Person invalidPerson = new Person(
                "12badidAB",
                "Jane",
                "Smith",
                "32 | Highland Street | Sydney | New South Wales | Australia",
                "15/11/2000"
        );
        assertFalse(invalidPerson.addPerson());
    }

    @Test
    public void testUpdatePersonalDetails_ValidUpdate_ShouldReturnTrue() {
        person.addPerson();  // First add the person
        assertTrue(person.updatePersonalDetails(null, "Jake", null, null, null));
    }

    @Test
    public void testUpdatePersonalDetails_Under18_Address_Update_ShouldReturnFalse() {
        Person under18Person = new Person(
                "56@#df&*AB",
                "Jane",
                "Smith",
                "32 | Highland Street | Melbourne | Victoria | Australia",
                "15-11-2021"
        );

        assertFalse(under18Person.updatePersonalDetails(null, null, null, "33 | Highland Street | Melbourne | Victoria | Australia", null));
    }

    @Test
    public void testUpdatePersonalDetails_BirthdayChangedOnly_ShouldReturnTrue() {
        person.addPerson();
        assertTrue(person.updatePersonalDetails(null, null, null, null, "15-11-2001"));
    }

    @Test
    public void testUpdatePersonalDetails_BirthdayAndNameChanged_ShouldReturnFalse() {
        person.addPerson();
        assertFalse(person.updatePersonalDetails(null, "Jake", null, null, "15-11-2001"));
    }

    @Test
    public void testUpdatePersonalDetails_EvenIDChange_ShouldReturnFalse() {
        Person evenIdPerson = new Person(
                "26@#df&*AB",
                "Jane",
                "Smith",
                "32 | Highland Street | Melbourne | Victoria | Australia",
                "15-11-2000"
        );

        assertFalse(evenIdPerson.updatePersonalDetails(null, "Jake", null, null, null));
    }

    @Test
    public void testAddDemeritPoints_Valid_ShouldReturnSuccess() {
        person.addPerson();
        String result = person.addDemeritPoints(3, LocalDate.now().minusMonths(1).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        assertEquals("Success", result);
    }

    @Test
    public void testAddDemeritPoints_InvalidPoints_ShouldReturnFailed() {
        person.addPerson();
        String result = person.addDemeritPoints(10, "15-11-2023");
        assertEquals("Failed", result);
    }

    @Test
    public void testAddDemeritPoints_InvalidDateFormat_ShouldReturnFailed() {
        person.addPerson();
        String result = person.addDemeritPoints(3, "2023-11-15");
        assertEquals("Failed", result);
    }

    @Test
    public void testAddDemeritPoints_Exceed6_Under21_ShouldReturnSuccess() {
        person.addPerson();
        person.addDemeritPoints(3, LocalDate.now().minusMonths(1).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        String result = person.addDemeritPoints(4, LocalDate.now().minusMonths(2).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        assertEquals("Success", result);
    }

    @Test
    public void testAddDemeritPoints_Exceed12_Over21_ShouldReturnSuccess() {
        person.addPerson();
        person.addDemeritPoints(5, LocalDate.now().minusMonths(1).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        person.addDemeritPoints(5, LocalDate.now().minusMonths(2).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        String result = person.addDemeritPoints(3, LocalDate.now().minusMonths(3).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        assertEquals("Success", result);
    }
}
