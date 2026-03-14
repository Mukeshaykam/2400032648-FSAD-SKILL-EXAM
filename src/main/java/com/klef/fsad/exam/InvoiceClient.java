package com.klef.fsad.exam;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class InvoiceClient
{
    public static void main(String[] args)
    {
        // Build SessionFactory from hibernate.cfg.xml
        try (SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory())
        {
            // Insert sample invoices
            insertInvoices(factory);

            // HQL: Select all invoices
            System.out.println("\n--- All Invoices (HQL) ---");
            selectAllInvoices(factory);

            // HQL: Select invoices by status
            System.out.println("\n--- Invoices with status 'Paid' (HQL) ---");
            selectInvoicesByStatus(factory, "Paid");

            // HQL: Select invoices with amount greater than a value
            System.out.println("\n--- Invoices with amount > 1000 (HQL) ---");
            selectInvoicesByAmountGreaterThan(factory, 1000.0);

            // HQL: Update invoice status
            System.out.println("\n--- Updating status of invoice with id=1 to 'Closed' (HQL) ---");
            updateInvoiceStatus(factory, 1, "Closed");

            // HQL: Select all invoices after update
            System.out.println("\n--- All Invoices after update (HQL) ---");
            selectAllInvoices(factory);

            // HQL: Delete an invoice
            System.out.println("\n--- Deleting invoice with id=2 (HQL) ---");
            deleteInvoice(factory, 2);

            // HQL: Select all invoices after delete
            System.out.println("\n--- All Invoices after delete (HQL) ---");
            selectAllInvoices(factory);
        }
    }

    private static void insertInvoices(SessionFactory factory)
    {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        session.persist(new Invoice("Alice", new Date(), "Paid", 1500.00));
        session.persist(new Invoice("Bob", new Date(), "Unpaid", 800.00));
        session.persist(new Invoice("Charlie", new Date(), "Paid", 2200.00));
        session.persist(new Invoice("Diana", new Date(), "Pending", 500.00));

        tx.commit();
        session.close();
        System.out.println("Sample invoices inserted.");
    }

    private static void selectAllInvoices(SessionFactory factory)
    {
        Session session = factory.openSession();
        Query<Invoice> query = session.createQuery("FROM Invoice", Invoice.class);
        List<Invoice> invoices = query.list();
        invoices.forEach(System.out::println);
        session.close();
    }

    private static void selectInvoicesByStatus(SessionFactory factory, String status)
    {
        Session session = factory.openSession();
        Query<Invoice> query = session.createQuery(
                "FROM Invoice WHERE status = :status", Invoice.class);
        query.setParameter("status", status);
        List<Invoice> invoices = query.list();
        invoices.forEach(System.out::println);
        session.close();
    }

    private static void selectInvoicesByAmountGreaterThan(SessionFactory factory, double amount)
    {
        Session session = factory.openSession();
        Query<Invoice> query = session.createQuery(
                "FROM Invoice WHERE amount > :amount", Invoice.class);
        query.setParameter("amount", amount);
        List<Invoice> invoices = query.list();
        invoices.forEach(System.out::println);
        session.close();
    }

    private static void updateInvoiceStatus(SessionFactory factory, int id, String newStatus)
    {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        MutationQuery query = session.createMutationQuery(
                "UPDATE Invoice SET status = :status WHERE id = :id");
        query.setParameter("status", newStatus);
        query.setParameter("id", id);
        int updated = query.executeUpdate();
        tx.commit();
        session.close();
        System.out.println(updated + " invoice(s) updated.");
    }

    private static void deleteInvoice(SessionFactory factory, int id)
    {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        MutationQuery query = session.createMutationQuery(
                "DELETE FROM Invoice WHERE id = :id");
        query.setParameter("id", id);
        int deleted = query.executeUpdate();
        tx.commit();
        session.close();
        System.out.println(deleted + " invoice(s) deleted.");
    }
}
