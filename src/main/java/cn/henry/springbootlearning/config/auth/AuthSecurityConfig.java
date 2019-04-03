package cn.henry.springbootlearning.config.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class AuthSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     *  http
     *                 .authorizeRequests()   //authorizeRequests() 定义哪些URL需要被保护、哪些不需要被保护
     *                 .antMatchers("/user/**", "/news/**").permitAll()  //这些请求 不登陆 即可访问
     *                 .anyRequest().authenticated()  //其他任何请求 必须需要认证
     *                 .and()
     *                 .formLogin().loginPage("/login")  //设置表单提交 登陆接口
     *                 .defaultSuccessUrl("/user")  //设置默认登录成功跳转页面
     *                 .failureUrl("/login?error").permitAll() //设置登陆失败页
     *                 .and()
     *                 .rememberMe()  //开启cookie保存用户数据
     *                 .tokenValiditySeconds(60 * 60) //设置cookie有效期
     *                 .key("") //设置cookie的私钥
     *                 .and()
     *                 .logout() //默认注销行为为logout，可以通过下面的方式来修改
     *                 .logoutUrl("/logout")
     *                 .logoutSuccessUrl("")  //设置注销成功后跳转页面，默认是跳转到登录页面
     *                 .permitAll()
     *                 .and()
     *                 .csrf().disable(); ////禁用CSRF保护  （即所有处理来自浏览器的请求需要是CSRF保护，如果后台服务是提供API调用那么可能就要禁用CSRF保护）
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/ping").permitAll()
                .anyRequest().authenticated()
        .and()
        .httpBasic()
        .authenticationEntryPoint( new RestAuthenticationEntryPoint());
    }
}
