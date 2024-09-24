package pe.edu.cibertec.rueditas_t1_front.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.cibertec.rueditas_t1_front.dto.PlacaRequestDTO;
import pe.edu.cibertec.rueditas_t1_front.dto.PlacaResponseDTO;
import pe.edu.cibertec.rueditas_t1_front.viewmodel.PlacaModel;

@Controller
@RequestMapping("/placa")
public class PlacaController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/buscar")
    public String buscarPlaca(Model model) {
        PlacaModel responseModel = new PlacaModel("00", "", "", "", "", "", "");
        model.addAttribute("responseModel", responseModel);
        return "buscar";
    }

    @PostMapping("/resultado")
    public String resultado(@RequestParam("nroPlaca") String nroPlaca, Model model) {

        if (nroPlaca == null || nroPlaca.trim().length() == 0 || nroPlaca.length() != 8) {
            PlacaModel responseModel = new PlacaModel("01", "Error: Debe ingresar una placa correcta (8 caracteres)", "", "", "", "", "");
            model.addAttribute("responseModel", responseModel);
            return "buscar";
        }

        try {
            String endpoint = "http://localhost:8081/placa/buscar";
            PlacaRequestDTO placaRequestDTO = new PlacaRequestDTO(nroPlaca);
            PlacaResponseDTO placaResponseDTO = restTemplate.postForObject(endpoint, placaRequestDTO, PlacaResponseDTO.class);
            if (placaResponseDTO.codigo().equals("00")) {

                PlacaModel placaModel = new PlacaModel("00", "", placaResponseDTO.marca(), placaResponseDTO.modelo(), placaResponseDTO.nroAsientos(), placaResponseDTO.precio(), placaResponseDTO.color());
                model.addAttribute("responseModel", placaModel);
                return "resultado";
            } else {

                PlacaModel placaModel = new PlacaModel("02", "Placa no encontrada", "", "", "", "", "");
                model.addAttribute("responseModel", placaModel);
                return "buscar";
            }

        } catch (Exception e) {
            PlacaModel responseModel = new PlacaModel("99", "Error: Ocurrió un problema al buscar la placa", "", "", "", "", "");
            model.addAttribute("responseModel", responseModel);
            System.out.println(e.getMessage());
            return "buscar";
        }
    }
}
