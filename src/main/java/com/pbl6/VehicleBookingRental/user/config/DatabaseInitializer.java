package com.pbl6.VehicleBookingRental.user.config;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Permission;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.permissionRole.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        log.info(" >>> Database initialization started");
        long countRole = this.roleRepository.count();
        long countPermission = this.permissionRepository.count();
        long countAccounts = this.accountRepository.count();

        if(countRole == 0) {
            ArrayList<Role> roles = new ArrayList<>();
            roles.add(new Role("ADMIN"));
            roles.add(new Role("USER"));
            roles.add(new Role("BUS_PARTNER"));
            roles.add(new Role("CAR_RENTAL_PARTNER"));
            roles.add(new Role("DRIVER"));
            this.roleRepository.saveAll(roles);
            log.info(">>> Initialize roles successfully");
        }
        if(countPermission == 0) {
            ArrayList<Permission> permissions = new ArrayList<>();
            permissions.add(new Permission("GET_ALL_ACCOUNT", "fetch all accounts in system"));

            permissions.add(new Permission("GET_ALL_REGISTER_BUSINESS_PARTNER", "see all register form of business partner"));
            permissions.add(new Permission("VIEW_REGISTER_BUSINESS_PARTNER", "see detail register form of business partner"));
            permissions.add(new Permission("VERIFY_REGISTER_BUSINESS_PARTNER", "confirm register business partner"));
            permissions.add(new Permission("CANCEL_BUSINESS_PARTNER", "cancel partnership business partner"));

            permissions.add(new Permission("GET_ALL_REGISTER_DRIVER", "see all register form of driver"));
            permissions.add(new Permission("VIEW_REGISTER_DRIVER", "see detail register form of driver"));
            permissions.add(new Permission("VERIFY_REGISTER_DRIVER", "confirm register driver"));
            permissions.add(new Permission("CANCEL_DRIVER", "cancel partnership driver"));

            this.permissionRepository.saveAll(permissions);
            log.info(">>> Initialize permission for admin successfully");
            // Add permissions for role admin
            Role adminRole = this.roleRepository.findByName("ADMIN").get();
            adminRole.setPermissions(permissions);
            this.roleRepository.save(adminRole);
            log.info(">>> Add permission for role admin successfully");

        }

        if(countAccounts == 0) {
            Account accoundAdmin = new Account();
            accoundAdmin.setEmail("admin@gmail.com");
            accoundAdmin.setPassword(passwordEncoder.encode("admin_pbl6"));
            accoundAdmin.setName("I am Admin");
            accoundAdmin.setActive(true);
            accoundAdmin.setVerified(true);
            this.accountRepository.save(accoundAdmin);
            AccountRole accountRole = new AccountRole();
            accountRole.setAccount(accoundAdmin);
            accountRole.setRole(this.roleRepository.findByName("ADMIN").get());
            this.accountRoleRepository.save(accountRole);
            log.info(">>> Create account admin successfully");
        }

        if(countRole > 0 && countPermission > 0 && countAccounts > 0) {
            log.info(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        }else{
            log.info(">>> END INIT DATABASE");
        }

    }
}
