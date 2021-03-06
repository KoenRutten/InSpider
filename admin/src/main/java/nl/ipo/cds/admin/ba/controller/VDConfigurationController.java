/**
 * 
 */
package nl.ipo.cds.admin.ba.controller;

import java.util.List;

import javax.validation.Valid;

import nl.ipo.cds.dao.metadata.MetadataDao;
import nl.ipo.cds.domain.metadata.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controller class for maintenance of View and DownloadServices.
 * The data updated here is used by the GetCapabilities-requests
 * 
 * @author eshuism
 * 20 jan 2012
 */
@Controller
@RequestMapping("/ba/vdconfig")
public class VDConfigurationController {

	@Autowired
	private MetadataDao metadataDao;

	@ModelAttribute("roleFunction")
	String getRoleFunction(){
		return "beheerder";
	}

	@Autowired
	private String inspireHost;
	
	@Autowired
	private String inspireGetCapabilitiesRequestTemplate;
	
	@Autowired
	private String inspireGetCapabilitiesRequestWMSVersion;
	
	@Autowired
	private String inspireGetCapabilitiesRequestWFSVersion;
	
	@ModelAttribute(value="inspireHost")
	public String getInspireHost() {
		return inspireHost;
	}
	
	@ModelAttribute(value="inspireGetCapabilitiesRequestTemplate")
	public String getInspireGetCapabilitiesRequestTemplate() {
		return inspireGetCapabilitiesRequestTemplate;
	}
	
	@ModelAttribute (value = "inspireGetCapabilitiesRequestWMSVersion")
	public String getInspireGetCapabilitiesRequestWMSVersion () {
		return inspireGetCapabilitiesRequestWMSVersion;
	}
	
	@ModelAttribute (value = "inspireGetCapabilitiesRequestWFSVersion")
	public String getInspireGetCapabilitiesRequestWFSVersion () {
		return inspireGetCapabilitiesRequestWFSVersion;
	}
	
	/**
	 * Only necessary for correct binding when doing submit/POST. Make sure that the objects, being command-objects,
	 * are generated by Hibernate and not new Objects created by Spring MVC
	 * 
	 * @param serviceId
	 * @param model
	 * @return
	 */
	@ModelAttribute("service")
	public Service getServiceObject (@RequestParam(required=false, value="serviceId") Long serviceId, Model model) {
		Service service = serviceId != null ? this.metadataDao.getService(serviceId) : null;
		// Remove all keywords when saving the service
		if(service != null){
			/* Remove all keywords when saving the service. Otherwise keywords are never removed, because they are always
			 * populated here
			 */
			service.getServiceIdentification().getKeywords().clear();
		}
		return service;
	}

	/**
	 * Parameter for storing the selected (active) (folder)tab of the client
	 * @param serviceTabId
	 * @return
	 */
	@ModelAttribute("serviceTabId")
	public String serviceTabId (@RequestParam(required=false, value="serviceTabId") String serviceTabId) {
		return serviceTabId;
	}

	@ModelAttribute("services")
	public List<Service> services (Model model) {
		List<Service> services = this.metadataDao.getAllServices();
		return services;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index (Model model) {
		List<Service> services = this.metadataDao.getAllServices();
		if(services.size()>0){
			return "redirect:/ba/vdconfig/service/" + services.get(0).getId();
		} else return "/ba/vdconfig";
	}

	@RequestMapping(value = "service/{serviceId}", method = RequestMethod.GET)
	public String getService (@PathVariable(value="serviceId") Long serviceId, Model model) {
		Service service = this.metadataDao.getService(serviceId);
		model.addAttribute("service", service);
		return "/ba/vdconfig";
	}

	@RequestMapping(value = "service/{serviceId}", method = RequestMethod.POST)
	public String UpdateService (@Valid @ModelAttribute(value="service") Service service, BindingResult bindingResult,
			SessionStatus status,@PathVariable(value="serviceId") Long serviceId, Model model) {
		if (bindingResult.hasErrors ()) {
			return "/ba/vdconfig";
		}
		service = this.metadataDao.update(service);

		// Redirect after POST pattern
		return "redirect:/ba/vdconfig/service/" + serviceId;
	}

}
