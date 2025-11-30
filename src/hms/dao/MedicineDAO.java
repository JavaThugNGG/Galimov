package hms.dao;

import hms.model.Medicine;

import java.util.List;
import java.util.function.Predicate;

public class MedicineDAO extends FileBasedDAO<Medicine, String> {

    public MedicineDAO() {
        super("data/medicines.txt");
    }

    @Override
    protected Medicine parseEntity(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            String[] data = line.split(",");
            if (data.length >= 6) {
                String medicineId = data[0].trim();
                String name = data[1].trim();
                String manufacturer = data[2].trim();
                String category = data[3].trim();

                if (medicineId.isEmpty() || name.isEmpty()) {
                    System.err.println("Неверные данные о лекарстве — отсутствует идентификатор или имя: " + line);
                    return null;
                }

                double price;
                int quantity;

                try {
                    price = Double.parseDouble(data[4].trim());
                    quantity = Integer.parseInt(data[5].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Неверный числовой формат в данных о лекарстве: " + line);
                    return null;
                }

                Medicine medicine = new Medicine(medicineId, name, manufacturer, price, quantity);

                if (!category.isEmpty() && !category.equals("null")) {
                    medicine.setCategory(category);
                }

                if (data.length >= 7 && !data[6].trim().isEmpty() && !data[6].trim().equals("null")) {
                    medicine.setDescription(data[6].trim());
                }

                return medicine;
            } else {
                System.err.println("Недостаточно полей данных в медицинской карте: " + line);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Ошибка анализа данных о лекарстве: " + line);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String entityToFileString(Medicine medicine) {
        if (medicine == null) return "";
        return medicine.toFileString();
    }

    @Override
    protected String getIdFromEntity(Medicine medicine) {
        return medicine != null ? medicine.getMedicineId() : null;
    }

    @Override
    protected boolean matchesProperty(Medicine medicine, String propertyName, Object value) {
        if (medicine == null || value == null) return false;

        String searchValue = value.toString().toLowerCase();

        switch (propertyName.toLowerCase()) {
            case "лекарствоид":
                return medicine.getMedicineId().toLowerCase().contains(searchValue);
            case "название":
                return medicine.getName().toLowerCase().contains(searchValue);
            case "производитель":
                return medicine.getManufacturer() != null &&
                        medicine.getManufacturer().toLowerCase().contains(searchValue);
            case "категория":
                return medicine.getCategory() != null &&
                        medicine.getCategory().toLowerCase().contains(searchValue);
            default:
                return false;
        }
    }

    public List<Medicine> findByCategory(String category) {
        return findByProperty("категория", category);
    }

    public List<Medicine> searchMedicines(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = query.toLowerCase();
        return findByPredicate(medicine ->
                (medicine.getMedicineId() != null && medicine.getMedicineId().toLowerCase().contains(searchTerm)) ||
                        (medicine.getName() != null && medicine.getName().toLowerCase().contains(searchTerm)) ||
                        (medicine.getManufacturer() != null && medicine.getManufacturer().toLowerCase().contains(searchTerm)) ||
                        (medicine.getCategory() != null && medicine.getCategory().toLowerCase().contains(searchTerm))
        );
    }

    public boolean updateQuantity(String medicineId, int newQuantity) {
        if (medicineId == null || newQuantity < 0) return false;

        Medicine medicine = findById(medicineId);
        if (medicine != null) {
            medicine.setQuantity(newQuantity);
            return update(medicine);
        }
        return false;
    }

    public boolean isInStock(String medicineId, int requiredQuantity) {
        if (medicineId == null || requiredQuantity <= 0) return false;

        Medicine medicine = findById(medicineId);
        return medicine != null && medicine.getQuantity() >= requiredQuantity;
    }

    public boolean reduceQuantity(String medicineId, int quantityToReduce) {
        if (medicineId == null || quantityToReduce <= 0) return false;

        Medicine medicine = findById(medicineId);
        if (medicine != null && medicine.getQuantity() >= quantityToReduce) {
            medicine.setQuantity(medicine.getQuantity() - quantityToReduce);
            return update(medicine);
        }
        return false;
    }

    public List<Medicine> getLowStockMedicines(int threshold) {
        return findByPredicate(medicine -> medicine.getQuantity() < threshold);
    }

    public List<Medicine> getOutOfStockMedicines() {
        return findByPredicate(medicine -> medicine.getQuantity() == 0);
    }
}
