
import java.time.LocalDate;
import java.util.*;

interface shippable {

    double getWeight();

    String getName();

}

interface Expirable {

    boolean isExpired();
}

abstract class product {

    protected String Name;
    private final double Price;
    private int Quantity;

    public product(String name, double price, int quantity) {
        this.Name = name;
        this.Price = price;
        this.Quantity = quantity;
    }

    public double getPrice() {
        return this.Price;
    }

    public String getName() {
        return this.Name;
    }

    public int getQuantity() {
        return this.Quantity;
    }

    public void incrementCount(int add) {
        this.Quantity += add;
    }

    public void decrementCount(int purchased) {

        Quantity -= purchased;
    }

}

class productNormal extends product {

    public productNormal(String name, double price, int quantity) {
        super(name, price, quantity);
    }

}

class productExp extends product implements Expirable {

    protected LocalDate expiryDate;

    public productExp(LocalDate expiryDate, String name, double price, int quantity) {
        super(name, price, quantity);
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean isExpired() {
        return !LocalDate.now().isBefore(this.expiryDate);
    }

}

class productShip extends product implements shippable {

    protected double Weight;

    public productShip(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.Weight = weight;
    }

    @Override
    public double getWeight() {
        return this.Weight;
    }

    @Override
    public String getName() {
        return this.Name;
    }

}

class productShipExp extends product implements Expirable, shippable {

    protected double Weight;
    protected LocalDate expiryDate;

    public productShipExp(LocalDate expiryDate, String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.expiryDate = expiryDate;
        this.Weight = weight;
    }

    @Override
    public double getWeight() {
        return this.Weight;
    }

    @Override
    public String getName() {
        return this.Name;
    }

    @Override
    public boolean isExpired() {
        return !LocalDate.now().isBefore(this.expiryDate);
    }

}

class Customer {

    long balance;

    public Customer(long balance) {
        if (balance < 0) {
            balance = 0;
            return;
        }
        this.balance = balance;
    }

    public void addBalance(int balance) {
        if (balance < 0) {
            return;
        }
        this.balance += balance;
    }

    public void removeBalance(double balance) {
        if (balance < 0) {
            return;
        }
        this.balance -= balance;
    }
}

class Cart {

    Map<product, Integer> list = new HashMap<>();

    //this function checks adding an element to the cart
    public void add(product added, int count) {
        if (added == null || count < 0) {
            return;
        }
        if (added.getQuantity() >= count) {
            list.merge(added, count, Integer::sum);
        } else {
            System.out.println("couldnt add item you ordered more than the available stock");
        }

    }

    //this function checks conditions like empty cart, insuffecient balance, expiry date and stock
    public Map<shippable, Integer> buy(long balance) {
        if (this.list.isEmpty()) {
            System.err.println("Error: Cart is empty.");
            return null;
        }
        Map<shippable, Integer> shipp = new HashMap<>();
        long subTotal = 0;

        for (Map.Entry<product, Integer> entry : list.entrySet()) {
            product key = entry.getKey();
            Integer value = entry.getValue();
            if (key instanceof Expirable && ((Expirable) key).isExpired()) {
                System.err.println("Error: " + key.getName() + " is expired.");
                return null;
            }
            if (key.getQuantity() < value) {
                System.err.println("Error: " + key.getName() + "'s stock is not enough.");
                return null;
            }
            if (key instanceof shippable ship) {
                shipp.put(ship, value);
                subTotal += Math.ceil(ship.getWeight() * 0.02);
            }
            subTotal += value * key.getPrice();
        }
        if (balance < subTotal) {
            System.err.println("Error: Balance is not enough.");

            return null;
        }
        return shipp;
    }

    //this function prints the reciept
    public boolean print(double totalWeight, Customer c1) {
        Map<product, Integer> mp = this.list;
        long subTotal = 0;

        System.out.println("\n** Checkout receipt **");
        for (Map.Entry< product, Integer> entry : mp.entrySet()) {
            product key = entry.getKey();
            Integer value = entry.getValue();
            subTotal += value * key.getPrice();
            key.decrementCount(value);
            System.out.println(value + "x " + key.getName() + "\t" + key.getPrice() * value
            );
        }
        System.out.println("----------------------");
        System.out.println("Subtotal $" + subTotal);
        System.out.println("Shipping $" + totalWeight * 0.02);
        System.out.println("Amount $" + (subTotal + Math.ceil(0.02 * totalWeight)));
        list.clear();
        c1.removeBalance((subTotal + Math.ceil(0.02 * totalWeight)));
        System.out.println("Customer's balance is now: " + c1.balance);
        return true;
    }
}

class ShippingService {

    public static double printNotice(Map<shippable, Integer> mp) {
        if (mp.isEmpty()) {
            return 0;
        }
        System.out.println("** Shipment notice **");
        double totalWeight = 0;
        for (Map.Entry< shippable, Integer> entry : mp.entrySet()) {
            shippable key = entry.getKey();
            Integer value = entry.getValue();
            totalWeight += key.getWeight();
            System.out.println(value + "x " + key.getName() + "\t" + key.getWeight() * value);
        }
        System.out.println("Totoal Package weight " + (totalWeight / 1000) + "kg \n");
        return totalWeight;
    }
}

public class Main {

    public static void main(String[] args) {
        //empty cart test
        System.out.println("test1");
        test1();
        System.err.println("-----------------------------------------------------------------------------------------");
        //item is sold out 
        System.out.println("test2");
        test2();
        System.err.println("-----------------------------------------------------------------------------------------");

        //expired item
        System.out.println("test3");

        test3();
        System.err.println("-----------------------------------------------------------------------------------------");

        //not enough balance
        System.out.println("test4");

        test4();
        System.err.println("-----------------------------------------------------------------------------------------");

        //not enough balance after shipping fees
        System.out.println("test5");

        test5();
        System.err.println("-----------------------------------------------------------------------------------------");

        //passed all checks
        System.out.println("test6");

        test6();
    }

    private static void checkout(Customer custom, Cart car) {
        System.out.println("CONSOLE OUTPUT:");

        Map<shippable, Integer> shipp = car.buy(custom.balance);
        if (shipp == null) {
            System.out.println("Error Cannot make the transaction");
            return;
        }
        double Weight = 0;
        if (!shipp.isEmpty()) {
            Weight = ShippingService.printNotice(shipp);
        }
        car.print(Weight, custom);

    }

    //empty cart test
    public static void test1() {
        Cart cart = new Cart();
        Customer custom = new Customer(20);
        checkout(custom, cart);
    }

    //not enough stock (if no enough stock when adding to cart addition is cancelled )
    public static void test2() {
        product scratchCard = new productNormal("scratch", 20, 1);
        Customer custom = new Customer(20000);
        Cart cart = new Cart();
        Cart cart1 = new Cart();
        cart.add(scratchCard, 1);
        cart1.add(scratchCard, 1);
        checkout(custom, cart1);
        checkout(custom, cart);
    }

    //expired product
    public static void test3() {
        Cart cart = new Cart();
        Customer custom = new Customer(20);
        product scratchCard = new productNormal("scratch", 1, 1);
        product cheese = new productShipExp(LocalDate.now().minusDays(2), "cheese", 1, 2, 200);
        cart.add(scratchCard, 1);
        cart.add(cheese, 2);
        checkout(custom, cart);
    }

    //not enough balance
    public static void test4() {
        Cart cart = new Cart();
        Customer custom = new Customer(20);
        product scratchCard = new productNormal("scratch", 21, 1);
        cart.add(scratchCard, 1);
        checkout(custom, cart);
    }

    //not enough balance beacuase of shipping fees
    public static void test5() {
        Cart cart = new Cart();
        Customer custom = new Customer(20);
        product cheese = new productShipExp(LocalDate.now().plusDays(2), "cheese", 20, 2, 20000);
        cart.add(cheese, 1);
        checkout(custom, cart);
    }

    //all cases passed 
    public static void test6() {
        Customer custom = new Customer(200);
        Cart cart = new Cart();
        product cheese = new productShipExp(LocalDate.now().plusDays(2), "cheese", 10, 2, 200);
        cart.add(cheese, 2);
        checkout(custom, cart);
    }

}
