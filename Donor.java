public class Donor {
    private final String name, bloodGroup, contact;

    public Donor(String name, String bloodGroup, String contact) {
        this.name = name;
        this.bloodGroup = bloodGroup.toUpperCase();
        this.contact = contact;
    }

    public String getName() { return name; }
    public String getBloodGroup() { return bloodGroup; }
    public String getContact() { return contact; }

    @Override
    public String toString() {
        return "Name: " + name + ", Blood Group: " + bloodGroup + ", Contact: " + contact;
    }
}