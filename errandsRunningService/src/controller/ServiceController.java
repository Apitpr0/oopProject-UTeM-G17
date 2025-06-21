package controller;

import dao.RequestDAO;
import model.ServiceRequest;
import model.UrgentServiceRequest;

import java.util.List;

public class ServiceController {

    // Submit a normal request
    public boolean submitRequest(ServiceRequest request) {
        return RequestDAO.submitRequest(request);
    }

    // Submit an urgent request (with extra RM charge)
    public boolean submitUrgentRequest(UrgentServiceRequest urgentRequest) {
        // You could also validate extra charge or urgency here
        return RequestDAO.submitRequest(urgentRequest);
    }

    // Retrieve all requests made by a specific customer
    public List<ServiceRequest> getCustomerRequests(int customerId) {
        return RequestDAO.getRequestsByCustomerId(customerId);
    }

    // Update status for a specific request (e.g., by admin or runner)
    public boolean updateRequestStatus(int requestId, String newStatus) {
        return RequestDAO.updateStatus(requestId, newStatus);
    }
}
