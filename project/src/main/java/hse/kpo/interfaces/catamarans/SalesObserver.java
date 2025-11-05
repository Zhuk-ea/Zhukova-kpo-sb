package hse.kpo.interfaces.catamarans;

import hse.kpo.domains.Customer;

interface SalesObserver {
    void onSale(Customer customer, ProductType type, int vin);
}
