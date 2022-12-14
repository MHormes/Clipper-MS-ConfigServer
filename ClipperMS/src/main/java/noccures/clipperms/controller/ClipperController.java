package noccures.clipperms.controller;

import noccures.clipperms.GeneralConfigurationProperties;
import noccures.clipperms.dto.ClipperConverter;
import noccures.clipperms.dto.ClipperDTO;
import noccures.clipperms.exceptions.DatabaseFailedOperationException;
import noccures.clipperms.exceptions.IncorrectInputException;
import noccures.clipperms.model.Clipper;
import noccures.clipperms.service.ClipperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//@RolesAllowed("USER")
@RestController
@RequestMapping("/clipper")
public class ClipperController {

    private final ClipperService clipperService;
    private final ClipperConverter clipperConverter = new ClipperConverter();

    @Autowired
    GeneralConfigurationProperties configuration;

    @Autowired
    private Environment env;


    @Autowired
    public ClipperController(ClipperService clipperService) {
        this.clipperService = clipperService;
    }

    @GetMapping("/secret")
    public ResponseEntity getMyName(){
        return ResponseEntity.ok().body(env.getProperty("clipper.name"));
    }

    @PostMapping("/add")
    public ClipperDTO addClipper(@RequestBody ClipperDTO clipperDTO) throws IncorrectInputException, DatabaseFailedOperationException {
        var clipperToAdd = clipperConverter.convertDTOtoModel(clipperDTO);
        Clipper addedClipperReturn;
        if(clipperDTO.getSeriesId() != null){
             addedClipperReturn = clipperService.addClipper(clipperToAdd, clipperDTO.getSeriesId().getId());
            return clipperConverter.convertModelToDTO(addedClipperReturn);
        }else{
             addedClipperReturn = clipperService.addClipper(clipperToAdd, null);
            return clipperConverter.convertModelNoSeriesToDTO(addedClipperReturn);
        }
    }


    @GetMapping("/{id}")
    public ClipperDTO getClipperWithId(@PathVariable(value = "id") String id) throws IncorrectInputException {
        var clipperWithId = clipperService.getClipperWithId(id);
        if(clipperWithId.getSeriesId() == null){
            return clipperConverter.convertModelNoSeriesToDTO(clipperWithId);
        }
        return clipperConverter.convertModelToDTO(clipperWithId);
    }

    @PutMapping("/update/{id}")
    public ClipperDTO updateClipper(@PathVariable(value = "id") String id, @RequestBody ClipperDTO clipperDTO) throws IncorrectInputException, DatabaseFailedOperationException {
        var clipperWithUpdate = clipperConverter.convertDTOtoModel(clipperDTO);
        var updatedClipperReturn = clipperService.updateClipper(clipperWithUpdate);
        if(updatedClipperReturn.getSeriesId() == null){
            return clipperConverter.convertModelNoSeriesToDTO(updatedClipperReturn);
        }
        return clipperConverter.convertModelToDTO(updatedClipperReturn);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteClipper(@PathVariable (value = "id")String id) throws IncorrectInputException, DatabaseFailedOperationException {
        clipperService.deleteClipper(id);
        return ResponseEntity.ok().build();
    }
}
