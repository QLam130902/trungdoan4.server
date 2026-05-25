package vn.homthugopy.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import vn.homthugopy.unit.entity.Unit;
import vn.homthugopy.unit.repository.UnitRepository;
import vn.homthugopy.user.entity.User;
import vn.homthugopy.user.repository.UserRepository;

@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, UnitRepository unitRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// 1. Seed danh sách đơn vị nếu chưa tồn tại
			if (unitRepository.count() == 0) {
				// Trung đoàn
				unitRepository.save(new Unit("TRUNG_DOAN_4", "Trung đoàn Bộ binh 4", null));

				// Các Đại đội trực thuộc Trung đoàn (8 đại đội)
				String[] directCompanies = {"c14", "c15", "c16", "c17", "c18", "c20", "c24", "c25"};
				for (String c : directCompanies) {
					unitRepository.save(new Unit(c, "Đại đội " + c.substring(1) + " trực thuộc", "TRUNG_DOAN_4"));
				}

				// Các Tiểu đoàn
				unitRepository.save(new Unit("TD1", "Tiểu đoàn 1", "TRUNG_DOAN_4"));
				unitRepository.save(new Unit("TD2", "Tiểu đoàn 2", "TRUNG_DOAN_4"));
				unitRepository.save(new Unit("TD3", "Tiểu đoàn 3", "TRUNG_DOAN_4"));

				// Đại đội trực thuộc Tiểu đoàn 1
				unitRepository.save(new Unit("c1", "Đại đội 1", "TD1"));
				unitRepository.save(new Unit("c2", "Đại đội 2", "TD1"));
				unitRepository.save(new Unit("c3", "Đại đội 3", "TD1"));
				unitRepository.save(new Unit("c4", "Đại đội 4", "TD1"));
				unitRepository.save(new Unit("KTT_TD1", "Khối trực thuộc tiểu đoàn 1", "TD1"));

				// Đại đội trực thuộc Tiểu đoàn 2
				unitRepository.save(new Unit("c5", "Đại đội 5", "TD2"));
				unitRepository.save(new Unit("c6", "Đại đội 6", "TD2"));
				unitRepository.save(new Unit("c7", "Đại đội 7", "TD2"));
				unitRepository.save(new Unit("c8", "Đại đội 8", "TD2"));
				unitRepository.save(new Unit("KTT_TD2", "Khối trực thuộc tiểu đoàn 2", "TD2"));

				// Đại đội trực thuộc Tiểu đoàn 3
				unitRepository.save(new Unit("c9", "Đại đội 9", "TD3"));
				unitRepository.save(new Unit("c10", "Đại đội 10", "TD3"));
				unitRepository.save(new Unit("c11", "Đại đội 11", "TD3"));
				unitRepository.save(new Unit("c12", "Đại đội 12", "TD3"));
				unitRepository.save(new Unit("KTT_TD3", "Khối trực thuộc tiểu đoàn 3", "TD3"));

				System.out.println("Đã khởi tạo dữ liệu cây đơn vị Trung đoàn 4 thành công!");
			}

			// 2. Seed tài khoản Admin
			if (!userRepository.existsByUsername("admin")) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("aATrungdoan4aA@"));
				admin.setFullName("Chỉ huy Trung đoàn 4");
				admin.setRank("Thượng tá");
				admin.setPosition("Quản trị viên Hệ thống");
				admin.setRole("ROLE_ADMIN");
				admin.setPhone("0909090909");
				admin.setUnitCode("TRUNG_DOAN_4");
				userRepository.save(admin);
				System.out.println("Đã khởi tạo tài khoản Admin mặc định thành công!");
			} else {
				// Đảm bảo admin cũ cũng được cập nhật unitCode
				User admin = userRepository.findByUsername("admin").orElse(null);
				if (admin != null && admin.getUnitCode() == null) {
					admin.setUnitCode("TRUNG_DOAN_4");
					userRepository.save(admin);
				}
			}

			// 3. Seed tài khoản cán bộ Nguyễn Văn Tuấn (Cán bộ Ban dân vận trung đoàn)
			if (!userRepository.existsByUsername("tuannvt")) {
				User tuan = new User();
				tuan.setUsername("tuannvt");
				tuan.setPassword(passwordEncoder.encode("Trungdoan4@2026"));
				tuan.setFullName("Nguyễn Văn Tuấn");
				tuan.setRank("Trung tá");
				tuan.setPosition("Ban Dân vận, Trung đoàn 4");
				tuan.setRole("ROLE_OFFICER");
				tuan.setPhone("0989496685");
				tuan.setUnitCode("TRUNG_DOAN_4");
				userRepository.save(tuan);
				System.out.println("Đã khởi tạo tài khoản cán bộ Nguyễn Văn Tuấn thành công!");
			} else {
				User tuan = userRepository.findByUsername("tuannvt").orElse(null);
				if (tuan != null && tuan.getUnitCode() == null) {
					tuan.setUnitCode("TRUNG_DOAN_4");
					userRepository.save(tuan);
				}
			}

			// 4. Seed tài khoản cán bộ Tiểu đoàn 1
			if (!userRepository.existsByUsername("cb_tieu_doan_1")) {
				User d1Leader = new User();
				d1Leader.setUsername("cb_tieu_doan_1");
				d1Leader.setPassword(passwordEncoder.encode("D1Leader@2026"));
				d1Leader.setFullName("Lê Minh D1");
				d1Leader.setRank("Thiếu tá");
				d1Leader.setPosition("Tiểu đoàn trưởng D1");
				d1Leader.setRole("ROLE_OFFICER");
				d1Leader.setPhone("0912345678");
				d1Leader.setUnitCode("TD1");
				userRepository.save(d1Leader);
				System.out.println("Đã khởi tạo tài khoản cán bộ Tiểu đoàn 1!");
			}

			// 5. Seed tài khoản cán bộ Đại đội 1
			if (!userRepository.existsByUsername("cb_dai_doi_1")) {
				User c1Leader = new User();
				c1Leader.setUsername("cb_dai_doi_1");
				c1Leader.setPassword(passwordEncoder.encode("C1Leader@2026"));
				c1Leader.setFullName("Trần Văn C1");
				c1Leader.setRank("Thượng úy");
				c1Leader.setPosition("Đại đội trưởng C1");
				c1Leader.setRole("ROLE_OFFICER");
				c1Leader.setPhone("0977665544");
				c1Leader.setUnitCode("c1");
				userRepository.save(c1Leader);
				System.out.println("Đã khởi tạo tài khoản cán bộ Đại đội 1!");
			}
		};
	}
}
