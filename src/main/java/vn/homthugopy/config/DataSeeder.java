package vn.homthugopy.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import vn.homthugopy.user.entity.User;
import vn.homthugopy.user.repository.UserRepository;

@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Kiểm tra nếu DB chưa có tài khoản admin nào thì tự động tạo
			if (!userRepository.existsByUsername("admin")) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("aATrungdoan4aA@"));
				admin.setFullName("Chỉ huy");
				admin.setRank("Chỉ huy trưởng");
				admin.setPosition("Quản trị viên Hệ thống");
				admin.setRole("ROLE_ADMIN");
				admin.setPhone(null); // Admin không cần SĐT liên hệ
				userRepository.save(admin);
				System.out.println("Đã khởi tạo tài khoản Admin mặc định thành công!");
			}

			// Seed tài khoản cán bộ xử lý
			if (!userRepository.existsByUsername("tuannvt")) {
				User tuan = new User();
				tuan.setUsername("tuannvt");
				tuan.setPassword(passwordEncoder.encode("Trungdoan4@2026"));
				tuan.setFullName("Nguyễn Văn Tuấn");
				tuan.setRank("Trung tá");
				tuan.setPosition("Trưởng Ban Dân vận, Sư đoàn 5");
				tuan.setRole("ROLE_OFFICER");
				tuan.setPhone("0989496685");
				userRepository.save(tuan);
				System.out.println("Đã khởi tạo tài khoản cán bộ Nguyễn Văn Tuấn thành công!");
			}
		};
	}
}
