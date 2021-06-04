package com.snp.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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

    @Autowired
    public DataLoader(UserService userService, ModuleService modService, TestReferenceService tfService, RoleService roleService) {
        this.userService = userService;
        this.modService = modService;
        this.tfService = tfService;
        this.roleService = roleService;
    }

	@Override
	public void run(ApplicationArguments args) throws Exception {		
		
		this.modService.save(new Module("Modules", "modules", "MODULE_NAME"));
		this.modService.save(new Module("Users", "users", "USER_NAME"));
		this.modService.save(new Module("Create Table", "createTable"));
		this.modService.save(new Module("H2 Console", "h2-console"));
		this.modService.save(new Module("Test Reference", "example_reference"));
		this.modService.save(new Module("Roles", "sys_user_role", "description"));
		
		this.roleService.save(new Role("admin", "Admin allows all CRUD operations and ability to access db."));
		this.roleService.save(new Role("guest", "Everyone gets guest by default."));
		
		this.userService.save(new User("admin", "admin", "admin"));
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

		
		for (int i = 0; i < lastNames.length; i++) {
			String fName = firstNames[_rand(firstNames)];
			String lName = lastNames[_rand(lastNames)];
			String uName = fName + " " + lName;
			User tmp = new User(fName, lName, uName);
			this.userService.save(tmp);
		}
		
		Iterable<User> users = userService.findAll();
		users.forEach(u -> {
			this.tfService.save(new TestReference(u));
		});
		
	}
	
	private static int _rand(String[] arr) {
		return (int) (Math.random() * arr.length - 1);
	}
}