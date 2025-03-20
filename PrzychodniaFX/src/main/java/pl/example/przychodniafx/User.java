package pl.example.przychodniafx;

public class User {
    private String name;
    private String surname;
    private String pesel;
    private String birthDate;
    private String phone;

    public User(String name, String surname, String pesel, String birthDate, String phone) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    // Gettery
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPesel() { return pesel; }
    public String getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }

    // Settery (DODANE)
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setPesel(String pesel) { this.pesel = pesel; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public void setPhone(String phone) { this.phone = phone; }
}
