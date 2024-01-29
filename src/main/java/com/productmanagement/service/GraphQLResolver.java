package com.productmanagement.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.productmanagement.model.Order;
import com.productmanagement.model.Product;
import com.productmanagement.model.ProductLine;
import com.productmanagement.model.Role;
import com.productmanagement.model.User;
import com.productmanagement.pageresults.PageResultOrder;
import com.productmanagement.pageresults.PageResultProduct;
import com.productmanagement.pageresults.PageResultProductLine;
import com.productmanagement.pageresults.PageResultUser;
import com.productmanagement.repo.OrderRepo;
import com.productmanagement.repo.ProductLinerepo;
import com.productmanagement.repo.ProductRepo;
import com.productmanagement.repo.UserRepo;

import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class GraphQLResolver implements GraphQLQueryResolver {
	private final UserRepo userRepository;
	private final ProductLinerepo productLineRepository;
	private final OrderRepo orderRepository;
	private final ProductRepo productRepository;

	public GraphQLResolver(UserRepo userRepository, ProductLinerepo productLineRepository, OrderRepo orderRepository,
			ProductRepo productRepository) {
		this.userRepository = userRepository;
		this.productLineRepository = productLineRepository;
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	public PageResultUser getUsers(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<User> userPage = userRepository.findAll(pageRequest);
		return new PageResultUser(userPage.getContent(), userPage.getNumber(), userPage.getSize(),
				userPage.getTotalElements(), userPage.getTotalPages());
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	public PageResultProductLine getProductLines(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<ProductLine> productLinePage = productLineRepository.findAll(pageRequest);
		return new PageResultProductLine(productLinePage.getContent(), productLinePage.getNumber(),
				productLinePage.getSize(), productLinePage.getTotalElements(), productLinePage.getTotalPages());
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	public PageResultOrder getOrders(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<Order> orderPage = orderRepository.findAll(pageRequest);
		return new PageResultOrder(orderPage.getContent(), orderPage.getNumber(), orderPage.getSize(),
				orderPage.getTotalElements(), orderPage.getTotalPages());
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	public PageResultProduct getProducts(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size,Sort.by(Sort.Direction.ASC, "stock"));
		Page<Product> productPage = productRepository.findAll(pageRequest);
		return new PageResultProduct(productPage.getContent(), productPage.getNumber(), productPage.getSize(),
				productPage.getTotalElements(), productPage.getTotalPages());
	}
	
	@PreAuthorize("hasAuthority('USER')")
	public User getUserById(Long userId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		Optional<User> optUser = userRepository.findByEmail(username);
		
		if(optUser.isEmpty()) {
			return null;
		}
		
		if(optUser.get().getRoles().contains(Role.USER)&&optUser.get().getId()!=userId) {
			return null;
		}
		
        return userRepository.findById(userId).orElse(null);
    }

	@PreAuthorize("hasAuthority('USER')")
    public ProductLine getProductLineById(Long productLineId) {
        return productLineRepository.findById(productLineId).orElse(null);
    }

	@PreAuthorize("hasAuthority('USER')")
    public Order getOrderById(Long orderId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		Optional<User> optUser = userRepository.findByEmail(username);
		
		if(optUser.isEmpty()) {
			return null;
		}
		
		Order order = orderRepository.findById(orderId).orElse(null);
		
		if(order==null) {
			return null;
		}
		
		if(optUser.get().getRoles().contains(Role.USER)&&order.getUser().getId()!=optUser.get().getId()) {
			return null;
		}
		
        return orderRepository.findById(orderId).orElse(null);
    }

	@PreAuthorize("hasAuthority('USER')")
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}
