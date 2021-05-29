package BusinessLayer.TransportsModule.Controllers;

import BusinessLayer.EmployeesModule.Controllers.StaffController;
import BusinessLayer.Interfaces.Controller;
import BusinessLayer.EmployeesModule.Objects.*;
import BusinessLayer.SuppliersModule.DayOfWeek;
import BusinessLayer.SuppliersModule.Order;
import BusinessLayer.TransportsModule.Objects.ItemContract;
import BusinessLayer.TransportsModule.Objects.Site;
import BusinessLayer.TransportsModule.Objects.Transport;
import BusinessLayer.TransportsModule.Objects.Truck;
import DataAccessLayer.TransportsDAO;
import DTO.ItemContractDTO;
import DTO.TransportDTO;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class Transports implements Controller<Transport> {
    private ArrayList<Transport> transports;
    private int ID = 0;
    private Trucks trucksController;
    private Sites sitesController;
    private StaffController staffController;
    private TransportsDAO DAO;

    private static Transports instance=null;

    public static Transports getInstance(){
        if(instance==null)
            instance=new Transports();
        return instance;
    }
    private Transports() {
        this.transports = new ArrayList<>();
        trucksController = Trucks.getInstance();
        sitesController = Sites.getInstance();
        staffController = StaffController.getInstance();
        DAO = new TransportsDAO();
    }

    public void addTransport(Date date, int weight, Driver driver, Truck truck, List<ItemContract> contracts, Site source) throws Exception {
        Transport toAdd = new Transport(date, weight, driver, truck, contracts, source, ID);
        transports.add(toAdd);
        ID ++;
        DAO.insert(toAdd.toDTO());
    }

    public ArrayList<Transport> getTransports() {
        return transports;
    }

    @Override
    public void Load() {
        this.transports = new ArrayList<>();
        ID = 0;
        try {
            List<TransportDTO> DTOS = DAO.getAll();
            for (TransportDTO el : DTOS) {
                List<ItemContract> ICS = new ArrayList<>();
                for (ItemContractDTO el2 : el.Contracts) {
                    ICS.add(new ItemContract(el2.ID,sitesController.getSite(el2.destination), el2.items, el2.passed));
                }
                this.transports.add(new Transport(Misc.Functions.StringToDate(el.date), el.weight, (Driver)staffController.getEmployeeByID(el.driver), trucksController.getTruck(el.truck), ICS, sitesController.getSite(el.source), el.ID));
                ID ++;
            }
        }
        catch (Exception e )
        {

        }
    }


    /**
     * This function should find an available transport within a week from the date issued with the order
     * With the given constraint of the fixed days the supplier is in the site destination
     * (meaning that the transport can only be executed on a day which is included in the fixedDays set).
     * @param order - the order to transport
     * @param siteDestination - the destination to pick the order from
     * @param fixedDays - the fixed days of the supplier (the constraint).
     * @return returns true if found a transport, returns false if no transport that fulfills the constraints was found.
     */
    public boolean requestTransport(Order order, String siteDestination, Set<DayOfWeek> fixedDays) {
        throw new NotImplementedException();
    }
}
