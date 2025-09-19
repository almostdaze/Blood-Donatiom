import java.util.*;

public class BloodInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public BloodInventory() {
        Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
              .forEach(group -> inventory.put(group, 0));
    }

    public void addBlood(String bloodGroup, int units) {
        bloodGroup = bloodGroup.toUpperCase();
        inventory.put(bloodGroup, inventory.getOrDefault(bloodGroup, 0) + units);
    }

    public boolean requestBlood(String bloodGroup, int units) {
        bloodGroup = bloodGroup.toUpperCase();
        int available = inventory.getOrDefault(bloodGroup, 0);
        if (available >= units) {
            inventory.put(bloodGroup, available - units);
            return true;
        }
        return false;
    }

    public Set<Map.Entry<String, Integer>> getEntries() {
        return inventory.entrySet();
    }
}