package com.shopme.admin.user;


import com.shopme.admin.exception.NotFoundException;
import com.shopme.admin.utils.FileUploadUtil;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@RestController
public class UserController {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    // to list of all users
    @GetMapping("/users/all")
    public ResponseEntity<List<User>> listAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.listAll());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> listAllFirstPage() {
        Page<User> page = userService.listAllPage(1,"firstName","asc",null);
        return ResponseEntity.status(HttpStatus.OK).body(page.getContent());
    }

    @GetMapping("/users/page/{pageNum}")
    public ResponseEntity<Map<String,Object>> listAllByPage(@PathVariable int pageNum, @Param("sortField") String sortField,
                                                    @Param("sortOrder") String sortOrder,@Param("keyword") String keyword) {

        Page<User> page = userService.listAllPage(pageNum,sortField,sortOrder,keyword);

        Map<String,Object> map = new HashMap<>();

        map.put("pageNumber",pageNum);
        map.put("totalElement",page.getTotalElements());
        map.put("totalPage",page.getTotalPages());
        map.put("data",page.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }


    // to list of all roles
    @GetMapping("/roles")
    public List<Role> listAllRoles() {
        return userService.listAllRoles();
    }

    // to save new user
    @PostMapping(value = "/users/save",consumes = {"multipart/form-data"})
    public ResponseEntity<User> saveUser(@Valid @RequestPart("user") User user,@RequestPart("image") MultipartFile multipartFile) throws IOException {

        Optional<User> byEmailID = userService.findByEmailID(user.getEmail());

        if (byEmailID.isPresent()){

            throw new NotFoundException("Email id already exist");
        }

        Set<Role> role = userService.getRole(user.getRoles());

        User savedUser;
        user.setRoles(role);
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setPhotos(fileName);
             savedUser = userService.saveUser(user);

            String uploadDir = "D:\\ShopMe-2\\shopmeproject\\Shopmewebparent\\Shopmebackend\\user-photos\\" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);

        } else {
            if (user.getPhotos().isEmpty())
                user.setPhotos(null);
             savedUser = userService.saveUser(user);

        }


        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

    }

    @PutMapping("/users/update/{id}")
    public ResponseEntity<Object> updateStudent(@Valid @RequestBody User user, @PathVariable int id) {

         userService.findById(id);

        user.setId(id);

        Optional<User> byEmailID = userService.findByEmailID(user.getEmail());

        if (byEmailID.isPresent() && byEmailID.get().getId()!=id ){
            throw new NotFoundException("EmailId already exist");
        }

        User savedUser = userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.OK).body(savedUser);
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        userService.deleteById(id);
        return new ResponseEntity<>("User successfully deleted!", HttpStatus.OK);
    }

    @PatchMapping("/users/{id}/enabled")
    public ResponseEntity<String> updateUserEnableStatus(@PathVariable("id") int id, @RequestParam("status") boolean status) {

        boolean updated = userService.updateUserEnableStatus(id, status);

        logger.debug("### id ",id," status ",status);

        if (updated) {
            String message = status ? "User id " + id + " enabled successfully" : "User id " + id + " disabled successfully";
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }



}


/*  @PostMapping("/users/edit")
    public ResponseEntity<Map<String, Object>> editUser(@RequestBody User user) {

        Optional<User> userOptional = userService.findById(user.getId());

        if (userOptional.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            ErrorDetails errorResponse = new ErrorDetails(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "User ID not found"
            );
            map.put("success", false);
            map.put("data", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        } else {

            userService.findByEmailID(user.getEmail());

            Set<Role> roles = userService.getRole(user.getRoles());
            user.setRoles(roles);
            User savedUser = userService.saveUser(user);

            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("data", savedUser);
            map.put("message", "Edited successfully");

            return ResponseEntity.status(HttpStatus.OK).body(map);
        }

    }*/

  /*      @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnableStatus(@PathVariable ("id") int id,@PathVariable("status") boolean status){

        userService.updateUserEnableStatus(id,status);

        return status ? "User id "+id+"Enabled successfully":"User id "+id+"disabled successfully";
    }
*/
