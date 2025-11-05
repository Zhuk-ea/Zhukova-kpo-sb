package hse.kpo.interfaces;
import hse.kpo.domains.Customer;
import hse.kpo.domains.Report;
import hse.kpo.enums.ProductionTypes;



public interface SalesObserver {
    void onSale(Customer customer, ProductionTypes type, int vin);
}
