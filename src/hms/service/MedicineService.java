package hms.service;

import hms.dao.MedicineDAO;
import hms.interfaces.ManagementService;
import hms.model.Medicine;
import hms.util.ValidationUtils;

import java.util.List;

public class MedicineService implements ManagementService<Medicine, String> {

    private MedicineDAO medicineDAO;

    public MedicineService() {
        medicineDAO = new MedicineDAO();
    }

    @Override
    public boolean add(Medicine medicine) {
        if (medicine == null) {
            return false;
        }

        if (!ValidationUtils.isValidId(medicine.getMedicineId()) ||
                !ValidationUtils.isValidName(medicine.getName()) ||
                !ValidationUtils.isValidPrice(medicine.getPrice()) ||
                !ValidationUtils.isValidQuantity(medicine.getQuantity())) {
            return false;
        }

        if (medicineDAO.exists(medicine.getMedicineId())) {
            return false;
        }

        return medicineDAO.save(medicine);
    }

    @Override
    public Medicine getById(String id) {
        if (!ValidationUtils.isValidId(id)) {
            return null;
        }
        return medicineDAO.findById(id);
    }

    @Override
    public List<Medicine> getAll() {
        return medicineDAO.findAll();
    }

    @Override
    public boolean update(Medicine medicine) {
        if (medicine == null) {
            return false;
        }

        if (!ValidationUtils.isValidId(medicine.getMedicineId()) ||
                !ValidationUtils.isValidName(medicine.getName()) ||
                !ValidationUtils.isValidPrice(medicine.getPrice()) ||
                !ValidationUtils.isValidQuantity(medicine.getQuantity())) {
            return false;
        }

        if (!medicineDAO.exists(medicine.getMedicineId())) {
            return false;
        }

        return medicineDAO.update(medicine);
    }

    @Override
    public boolean delete(String id) {
        if (!ValidationUtils.isValidId(id)) {
            return false;
        }
        return medicineDAO.delete(id);
    }

    @Override
    public List<Medicine> search(String query) {
        return medicineDAO.searchMedicines(query);
    }

    public List<Medicine> findByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAll();
        }
        return medicineDAO.findByCategory(category);
    }

    public boolean updateQuantity(String medicineId, int newQuantity) {
        if (!ValidationUtils.isValidId(medicineId) || !ValidationUtils.isValidQuantity(newQuantity)) {
            return false;
        }
        return medicineDAO.updateQuantity(medicineId, newQuantity);
    }

    public boolean isInStock(String medicineId, int requiredQuantity) {
        if (!ValidationUtils.isValidId(medicineId) || requiredQuantity <= 0) {
            return false;
        }
        return medicineDAO.isInStock(medicineId, requiredQuantity);
    }

    public boolean dispenseMedicine(String medicineId, int quantity) {
        if (!ValidationUtils.isValidId(medicineId) || quantity <= 0) {
            return false;
        }
        return medicineDAO.reduceQuantity(medicineId, quantity);
    }

    public String generateMedicineId() {
        return "M" + System.currentTimeMillis();
    }

    public List<Medicine> getLowStockMedicines() {
        return medicineDAO.getLowStockMedicines(10);
    }

    public List<Medicine> getOutOfStockMedicines() {
        return medicineDAO.getOutOfStockMedicines();
    }
}
