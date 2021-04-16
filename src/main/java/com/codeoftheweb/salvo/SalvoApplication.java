package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {

			//PLAYERS

			Player player1 = playerRepository.save(new Player("j.bauer@ctu.gov", passwordEncoder().encode("24")));
			Player player2 = playerRepository.save(new Player("c.obrian@ctu.gov", passwordEncoder().encode("42")));
			Player player3 = playerRepository.save(new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb")));
			Player player4 = playerRepository.save(new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole")));

			//GAMES

			Game game1 = gameRepository.save(new Game(LocalDateTime.now()));
			Game game2 = gameRepository.save(new Game(LocalDateTime.now().plusHours(1)));
			Game game3 = gameRepository.save(new Game(LocalDateTime.now().plusHours(2)));
			Game game4 = gameRepository.save(new Game(LocalDateTime.now().plusHours(3)));
			Game game5 = gameRepository.save(new Game(LocalDateTime.now().plusHours(4)));
			Game game6 = gameRepository.save(new Game(LocalDateTime.now().plusHours(5)));
			Game game7 = gameRepository.save(new Game(LocalDateTime.now().plusHours(6)));
			Game game8 = gameRepository.save(new Game(LocalDateTime.now().plusHours(7)));

			//GAMEPLAYER
			// associating everything that has to be with game and player with gamePlayer @Many-to-many

			GamePlayer gamePlayer1 = gamePlayerRepository.save(new GamePlayer(player1, game1));
			GamePlayer gamePlayer2 = gamePlayerRepository.save(new GamePlayer(player2, game1));

			GamePlayer gamePlayer3 = gamePlayerRepository.save(new GamePlayer(player1, game2));
			GamePlayer gamePlayer4 = gamePlayerRepository.save(new GamePlayer(player2, game2));

			GamePlayer gamePlayer5 = gamePlayerRepository.save(new GamePlayer(player2, game3));
			GamePlayer gamePlayer6 = gamePlayerRepository.save(new GamePlayer(player4, game3));

			GamePlayer gamePlayer7 = gamePlayerRepository.save(new GamePlayer(player2, game4));
			GamePlayer gamePlayer8 = gamePlayerRepository.save(new GamePlayer(player1, game4));

			GamePlayer gamePlayer9 = gamePlayerRepository.save(new GamePlayer(player4, game5));
			GamePlayer gamePlayer10 = gamePlayerRepository.save(new GamePlayer(player1, game5));

			GamePlayer gamePlayer11 = gamePlayerRepository.save(new GamePlayer(player3, game6));

			GamePlayer gamePlayer12 = gamePlayerRepository.save(new GamePlayer(player4, game7));

			GamePlayer gamePlayer13 = gamePlayerRepository.save(new GamePlayer(player3, game8));
			GamePlayer gamePlayer14 = gamePlayerRepository.save(new GamePlayer(player4, game8));

			//SHIPS - SALVOES
			// creating ships locations arrays
			// creating salvoes locations
			//

			List<String> ubication;
			Ship ship1 = shipRepository.save(new Ship("destroyer", gamePlayer1, ubication = Arrays.asList("H2", "H3", "H4")));
			Ship ship2 = shipRepository.save(new Ship("submarine", gamePlayer1, ubication = Arrays.asList("E1", "F1", "G1")));
			Ship ship3 = shipRepository.save(new Ship("patrolboat", gamePlayer1, ubication = Arrays.asList("B4", "B5")));

			Salvo salvo1 = salvoRepository.save(new Salvo(1, gamePlayer1, Arrays.asList("B5","C5","F1")));
			Salvo salvo2 = salvoRepository.save(new Salvo(2, gamePlayer1, Arrays.asList("F2","D5")));

			//

			Ship ship4 = shipRepository.save(new Ship("destroyer", gamePlayer2, ubication = Arrays.asList("B5", "C5", "D5")));
			Ship ship5 = shipRepository.save(new Ship("patrolboat", gamePlayer2, ubication = Arrays.asList("F1", "F2")));

			Salvo salvo3 = salvoRepository.save(new Salvo(1, gamePlayer2, Arrays.asList("B3","B4", "B5")));
			Salvo salvo4 = salvoRepository.save(new Salvo(2, gamePlayer2, Arrays.asList("E1", "H3", "A2")));

			//

			Ship ship6 = shipRepository.save(new Ship("destroyer", gamePlayer1, ubication = Arrays.asList("B5", "C5", "D5")));
			Ship ship7 = shipRepository.save(new Ship("patrolboat", gamePlayer1, ubication = Arrays.asList("C6", "C7")));

			Salvo salvo5 = salvoRepository.save(new Salvo(1, gamePlayer3, Arrays.asList("A2","A4", "G6")));
			Salvo salvo6 = salvoRepository.save(new Salvo(2, gamePlayer3, Arrays.asList("A3","H6")));

			//

			Ship ship8 = shipRepository.save(new Ship("submarine", gamePlayer2, ubication = Arrays.asList("A2", "A3", "A4")));
			Ship ship9 = shipRepository.save(new Ship("patrolboat", gamePlayer2, ubication = Arrays.asList("G6", "H6")));

			Salvo salvo7 = salvoRepository.save(new Salvo(1, gamePlayer4, Arrays.asList("B5","D5", "C7")));
			Salvo salvo8 = salvoRepository.save(new Salvo(2, gamePlayer4, Arrays.asList("C5","C6")));

			//

			Ship ship10 = shipRepository.save(new Ship("destroyer", gamePlayer2, ubication = Arrays.asList("B5", "C5", "D5")));
			Ship ship11 = shipRepository.save(new Ship("patrolboat", gamePlayer1, ubication = Arrays.asList("C6", "C7")));

			Salvo salvo9 = salvoRepository.save(new Salvo(1, gamePlayer5, Arrays.asList("G6","H6", "A4")));
			Salvo salvo10 = salvoRepository.save(new Salvo(2, gamePlayer5, Arrays.asList("A2","A3", "D8")));

			//

			Ship ship12 = shipRepository.save(new Ship("submarine", gamePlayer4, ubication = Arrays.asList("A2", "A3", "A4")));
			Ship ship13 = shipRepository.save(new Ship("patrolboat", gamePlayer4, ubication = Arrays.asList("G6", "H6")));

			Salvo salvo11 = salvoRepository.save(new Salvo(1, gamePlayer6, Arrays.asList("H1","H2", "H3")));
			Salvo salvo12 = salvoRepository.save(new Salvo(2, gamePlayer6, Arrays.asList("E1","F2", "G3")));

			//

			Ship ship14 = shipRepository.save(new Ship("destroyer", gamePlayer2, ubication = Arrays.asList("B5", "C5", "D5")));
			Ship ship15 = shipRepository.save(new Ship("patrolboat", gamePlayer2, ubication = Arrays.asList("C6", "C7")));

			Salvo salvo13 = salvoRepository.save(new Salvo(1, gamePlayer7, Arrays.asList("A3","A4", "A7")));
			Salvo salvo14 = salvoRepository.save(new Salvo(2, gamePlayer7, Arrays.asList("A2","G6", "H6")));

			//

			Ship ship16 = shipRepository.save(new Ship("submarine", gamePlayer1, ubication = Arrays.asList("A2", "A3", "A4")));
			Ship ship17 = shipRepository.save(new Ship("patrolboat", gamePlayer1, ubication = Arrays.asList("G6", "H6")));

			Salvo salvo18 = salvoRepository.save(new Salvo(1, gamePlayer8,Arrays.asList("B5","C6", "H1")));
			Salvo salvo19 = salvoRepository.save(new Salvo(2, gamePlayer8, Arrays.asList("C5","C7", "D5")));

			//

			Ship ship18 = shipRepository.save(new Ship("destroyer", gamePlayer4, ubication = Arrays.asList("B5", "C5", "D5")));
			Ship ship19 = shipRepository.save(new Ship("patrolboat", gamePlayer4, ubication = Arrays.asList("C6", "C7")));

			Salvo salvo20 = salvoRepository.save(new Salvo(1, gamePlayer9, Arrays.asList("A1","A2", "A3")));
			Salvo salvo21 = salvoRepository.save(new Salvo(2, gamePlayer9, Arrays.asList("G6","G7", "G8")));

			//

			Ship ship20 = shipRepository.save(new Ship("submarine", gamePlayer1, ubication = Arrays.asList("A2", "A3", "A4")));
			Ship ship21 = shipRepository.save(new Ship("patrolboat", gamePlayer1, ubication = Arrays.asList("G6", "H6")));

			Salvo salvo22 = salvoRepository.save(new Salvo(1, gamePlayer9, Arrays.asList("B5","B6", "C7")));
			Salvo salvo23 = salvoRepository.save(new Salvo(2, gamePlayer9, Arrays.asList("C6","D6", "E6")));
			Salvo salvo24 = salvoRepository.save(new Salvo(3, gamePlayer9, Arrays.asList("H1","H8")));

			//

			Ship ship22 = shipRepository.save(new Ship("destroyer", gamePlayer3, ubication = Arrays.asList("B5", "C5", "D5")));
			Ship ship23 = shipRepository.save(new Ship("patrolboat", gamePlayer3, ubication = Arrays.asList("C6", "C7")));

			//
			Ship ship24 = shipRepository.save(new Ship("destroyer", gamePlayer3, ubication = Arrays.asList("B5", "C5", "D5")));
			Ship ship25 = shipRepository.save(new Ship("patrolboat", gamePlayer3, ubication = Arrays.asList("C6", "C7")));

			//


			Ship ship26 = shipRepository.save(new Ship("submarine", gamePlayer4, ubication = Arrays.asList("A2", "A3", "A4")));
			Ship ship27 = shipRepository.save(new Ship("patrolboat", gamePlayer4, ubication = Arrays.asList("G6", "H6")));

			//SCORES

			Score score1 = scoreRepository.save(new Score(0.5, LocalDateTime.now(), player1, game1));
			Score score2 = scoreRepository.save(new Score(1.0, LocalDateTime.now(), player2, game1));

			Score score3 = scoreRepository.save(new Score(0.5, LocalDateTime.now(), player3, game2));
			Score score4 = scoreRepository.save(new Score(1.0, LocalDateTime.now(), player4, game2));


		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	//AUTHENTICATION
	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName -> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown userName: " + inputName);
			}

		});
	}
	}

//AUTORIZATION

	@EnableWebSecurity
    @Configuration
    class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/game_view/**").hasAuthority("USER")
				.antMatchers("/h2-console/**").permitAll()
				.antMatchers("/api/games").permitAll()
				//.anyRequest().authenticated()
				.and().csrf().ignoringAntMatchers("/h2-console/")
				.and().headers().frameOptions().sameOrigin();

		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

		private void clearAuthenticationAttributes(HttpServletRequest request) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			}
		}
	}


