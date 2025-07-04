
import java.time.LocalDate;
import java.util.*;

class product implements shippable {

    private String Name;
    private int Quantity;

    private float Price;

    //assuming constant fare per gm 
    private double Weight;
    private int Stock;
    private LocalDate expiryDate;

    public product(String name, int quantity, float price, double weight, int stock, LocalDate exp) {
        this.Name = name;
        this.Price = price;
        this.Quantity = quantity;
        this.Weight = weight;
        this.Stock = stock;
        this.expiryDate = exp;
    }

    @Override
    public double getWeight() {
        return this.Weight;
    }

    @Override
    public String getName() {
        return this.Name;
    }

    public boolean decrementCount(int purchased) {

        this.Stock -= purchased;
        if (this.Stock < 0) {
            this.Stock += purchased;
            return false;
        }
        return true;
    }
;

}

class Customer {

    int balance;

    public Customer(int balance) {
        this.balance = balance;
    }

    public void addBalance(int balance) {
        this.balance = balance;
    }
}

class Cart {

    Map<product, Integer> list = new HashMap<>();

    public void add(product added, int count) {
        if (added.decrementCount(count)) {
            list.merge(added, count, Integer::sum);
        }

    }

}

interface shippable {

    double getWeight();

    String getName();

}

public class Main {

    public static void main(String[] args) {
        Cart cart = new Cart();

    }
}
