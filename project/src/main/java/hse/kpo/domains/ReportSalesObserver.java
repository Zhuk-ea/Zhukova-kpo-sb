package hse.kpo.domains;

import hse.kpo.enums.ProductionTypes;
import hse.kpo.interfaces.SalesObserver;

class ReportSalesObserver implements SalesObserver {
    public void onSale(Customer customer, ProductionTypes type, int vin) {};
    Report generateReport() {};
}