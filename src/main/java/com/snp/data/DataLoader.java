package com.snp.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.snp.entity.HasRole;
import com.snp.entity.Module;
import com.snp.entity.Role;
import com.snp.entity.TestReference;
import com.snp.entity.User;
import com.snp.service.*;

@Component
public class DataLoader implements ApplicationRunner {

    private UserService userService;
    private ModuleService modService;
    private TestReferenceService tfService;
    private RoleService roleService;
    private HasRoleService hasRoleService;

    @Autowired
    public DataLoader(UserService userService, ModuleService modService, TestReferenceService tfService, RoleService roleService, HasRoleService hasRoleService) {
        this.userService = userService;
        this.modService = modService;
        this.tfService = tfService;
        this.roleService = roleService;
        this.hasRoleService = hasRoleService;
    }

	@Override
	public void run(ApplicationArguments args) throws Exception {		
		
		//create modules
		this.modService.save(new Module("Modules", "modules", "MODULE_NAME", "ben"));
		this.modService.save(new Module("Users", "users", "USER_NAME", "ben"));
		this.modService.save(new Module("Create Table", "createTable", "", "ben"));
		//this.modService.save(new Module("H2 Console", "h2-console", "", "ben"));
		this.modService.save(new Module("Test Reference", "example_reference", "", "ben"));
		this.modService.save(new Module("Roles", "sys_user_role", "name", "ben"));
		this.modService.save(new Module("User Roles", "sys_user_has_role", "user", "ben"));
		
		//create roles
		Role admin = new Role("Admin", "Admin allows all CRUD operations and ability to access db.", "ben");
		Role guest = new Role("Guest", "Everyone gets guest by default.", "ben");
		this.roleService.save(admin);
		this.roleService.save(guest);
		
		//create default admin user and associate admin role
		User adminUser = new User("admin", "admin", "admin", "ben");
		this.userService.save(adminUser);
		this.hasRoleService.save(new HasRole(admin, adminUser, "ben"));
		
		String[] firstNames = { "Adam", "Alex", "Aaron", "Ben", "Carl", "Dan", "David", "Edward", "Fred",
				"Frank", "George", "Hal", "Hank", "Ike", "John", "Jack", "Joe", "Larry", "Monte", "Matthew",
				"Mark", "Nathan", "Otto", "Paul", "Peter", "Roger", "Roger", "Steve", "Thomas", "Tim", "Ty",
				"Victor", "Walter"};   
		String[] lastNames = {"Anderson", "Ashwoon", "Aikin", "Bateman", "Bongard", "Bowers", "Boyd", "Cannon",
				"Cast", "Deitz", "Dewalt", "Ebner", "Frick", "Hancock", "Haworth", "Hesch", "Hoffman", "Kassing",
				"Knutson", "Lawless", "Lawicki", "Mccord", "McCormack", "Miller", "Myers", "Nugent", "Ortiz",
				"Orwig", "Ory", "Paiser", "Pak", "Pettigrew", "Quinn", "Quizoz", "Ramachandran", "Resnick",
				"Sagar", "Schickowski", "Schiebel", "Sellon", "Severson", "Shaffer", "Solberg", "Soloman",
				"Sonderling", "Soukup", "Soulis", "Stahl", "Sweeney", "Tandy", "Trebil", "Trusela", "Trussel",
				"Turco", "Uddin", "Uflan", "Ulrich", "Upson", "Vader", "Vail", "Valente", "Van Zandt", "Vanderpoel",
				"Ventotla", "Vogal", "Wagle", "Wagner", "Wakefield", "Weinstein", "Weiss", "Woo", "Yang", "Yates",
				"Yocum", "Zeaser", "Zeller", "Ziegler", "Bauer", "Baxster", "Casal", "Cataldi", "Caswell", "Celedon",
				"Chambers", "Chapman", "Christensen", "Darnell", "Davidson", "Davis", "DeLorenzo", "Dinkins", "Doran",
				"Dugelman", "Dugan", "Duffman", "Eastman", "Ferro", "Ferry", "Fletcher", "Fietzer", "Hylan", "Hydinger", 
				"Illingsworth", "Ingram", "Irwin", "Jagtap", "Jenson", "Johnson", "Johnsen", "Jones", "Jurgenson", "Kalleg",
				"Kaskel", "Keller", "Leisinger", "LePage", "Lewis", "Linde", "Lulloff", "Maki", "Martin", "McGinnis", "Mills", 
				"Moody", "Moore", "Napier", "Nelson", "Norquist", "Nuttle", "Olson", "Ostrander", "Reamer", "Reardon", "Reyes", 
				"Rice", "Ripka", "Roberts", "Rogers", "Root", "Sandstrom", "Sawyer", "Schlicht", "Schmitt", "Schwager", "Schutz",
				"Schuster", "Tapia", "Thompson", "Tiernan", "Tisler" };
		
		//create random users
		for (int i = 0; i < lastNames.length; i++) {
			String fName = firstNames[_rand(firstNames)];
			String lName = lastNames[_rand(lastNames)];
			String uName = fName + " " + lName;
			User tmp = new User(fName, lName, uName, "ben");
			this.userService.save(tmp);
		}
		
		//populate test reference table, this was used to test reference fields in the system as it was being developed
		//also assign each user the guest role
		Iterable<User> users = userService.findAll();
		users.forEach(u -> {
			this.tfService.save(new TestReference(u, "ben"));
			this.hasRoleService.save(new HasRole(guest, u, "ben"));
		});
		
	}
	
	private static int _rand(String[] arr) {
		return (int) (Math.random() * arr.length - 1);
	}
}