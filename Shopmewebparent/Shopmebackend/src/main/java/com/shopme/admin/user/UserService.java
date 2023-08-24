package com.shopme.admin.user;


import com.shopme.admin.exception.NotFoundException;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    public static final int USER_LIST_SIZE = 4;
    private UserRepository userRepository;

    private RolesRepository rolesRepository;

    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public Page<User> listAllPage(int pageNum, String sortField, String sortOrder, String keyword) {

        if (sortOrder == null )
            sortOrder ="asc";

        if (sortField == null )
            sortField ="id";

        Sort sort = Sort.by(sortField);
        sort = sortOrder.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, USER_LIST_SIZE, sort);

        if (keyword != null)
            return userRepository.findAll(keyword, pageable);

        return userRepository.findAll(pageable);
    }

    public List<Role> listAllRoles() {
        return rolesRepository.findAll();
    }

    public User saveUser(User user) {
        encodePassword(user);
        return userRepository.save(user);
    }

    public Set<Role> getRole(Set<Role> role) {

        Set<Role> roleSet = new HashSet<>();
        role.forEach(r -> {
            Optional<Role> optionalRole = rolesRepository.findById(r.getId());
            roleSet.add(optionalRole.get());
        });

        return roleSet;

    }

    public Optional<User> findByEmailID(String emailId) {

        Optional<User> userMail = userRepository.findByEmail(emailId);
        return userMail;


    }

    public void encodePassword(User user) {

        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
    }

    public Optional<User> findById(int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Userid not found");
        } else {
            return userOptional;
        }

    }

    public void deleteById(int id) {

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new NotFoundException("User id not exist");

        userRepository.deleteById(id);


    }

    public boolean updateUserEnableStatus(int id, boolean isEnable) {

        findById(id);

        try {
            userRepository.updateUserEnableStatus(id, isEnable);
            return true; // If the update operation doesn't throw an exception, consider it successful
        } catch (Exception e) {
            return false; // If an exception occurs, consider the update unsuccessful
        }
    }


}
