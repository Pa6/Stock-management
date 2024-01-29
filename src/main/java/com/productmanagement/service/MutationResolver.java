package com.productmanagement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.productmanagement.model.Order;
import com.productmanagement.model.Product;
import com.productmanagement.model.ProductLine;
import com.productmanagement.model.Role;
import com.productmanagement.model.User;
import com.productmanagement.model.input.OrderInput;
import com.productmanagement.model.input.ProductInput;
import com.productmanagement.model.input.ProductLineInput;
import com.productmanagement.model.input.UserInput;
import com.productmanagement.repo.OrderRepo;
import com.productmanagement.repo.ProductLinerepo;
import com.productmanagement.repo.ProductRepo;
import com.productmanagement.repo.UserRepo;

import graphql.kickstart.tools.GraphQLMutationResolver;

@Component
public class MutationResolver implements GraphQLMutationResolver {
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final UserRepo userRepository;
	private final ProductLinerepo productLineRepository;
	private final OrderRepo orderRepository;
	private final ProductRepo productRepository;

	public MutationResolver(UserRepo userRepository, ProductLinerepo productLineRepository, OrderRepo orderRepository,
			ProductRepo productRepository) {
		this.userRepository = userRepository;
		this.productLineRepository = productLineRepository;
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
		createDefaultUser();

	}

	/**
	 * When creating the order we reduce same quantity from stock of product
	 * as the product line quantity has.
	 */
	@PreAuthorize("hasAuthority('USER')")
	@Transactional
	public Order createOrder(OrderInput orderInput) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String username = userDetails.getUsername();
			Optional<User> optUser = userRepository.findByEmail(username);

			if (optUser.isEmpty()) {
				return null;
			}

			Order order = new Order();

			List<ProductLine> listProductLines = productLineRepository.findAllById(orderInput.getProductLineIds());

			if (listProductLines == null || listProductLines.isEmpty()) {
				return null;
			}

			for (ProductLine pl : listProductLines) {
				if (pl.getOrder() != null) {
					return null;
				}
				pl.getProduct().setStock(pl.getProduct().getStock() - pl.getQuantity());
				if (pl.getProduct().getStock() < 0) {
					return null;
				}
				productRepository.save(pl.getProduct());
				pl.setOrder(order);
			}
			productLineRepository.saveAll(listProductLines);

			order.setUser(optUser.get());
			order.setProductLines(listProductLines);

			order = orderRepository.save(order);
			for (ProductLine pl : listProductLines) {
				pl.setOrder(order);
			}
			productLineRepository.saveAll(listProductLines);
			return order;
		} catch (Exception e) {
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	public Product createProduct(ProductInput productInput) {
		try {
			Product product = new Product();
			product.setName(productInput.getName());
			product.setStock(productInput.getStock());
			product.setPrice(productInput.getPrice());
			product = productRepository.save(product);
			return product;
		} catch (Exception e) {
			return null;
		}
	}

	@PreAuthorize("hasAuthority('USER')")
	@Transactional
	public ProductLine createProductLine(ProductLineInput productLineInput) {
		try {
			ProductLine productLine = new ProductLine();
			Optional<Product> optproduct = productRepository.findById(productLineInput.getProductId());

			if (optproduct.isEmpty()) {
				return null;
			}
			if (optproduct.get().getStock() < productLineInput.getQuantity()) {
				return null;
			}
			productLine.setProduct(optproduct.get());
			productLine.setQuantity(productLineInput.getQuantity());

			productLine = productLineRepository.save(productLine);
			return productLine;
		} catch (Exception e) {
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	public User createUser(UserInput userInput) {
		try {
			if (userInput.getRoles().isEmpty()) {
				return null;
			}
			if (userInput.getEmail() == null) {
				return null;
			}
			if(userRepository.findByEmail(userInput.getEmail()).isPresent()) {
				return null;
			}
			if (userInput.getName() == null) {
				return null;
			}
			if (userInput.getPassword() == null) {
				return null;
			}

			User user = new User();
			user.setName(userInput.getName());
			user.setEmail(userInput.getEmail());
			user.setRoles(userInput.getRoles());
			user.setPassword(passwordEncoder.encode(userInput.getPassword()));
			user = userRepository.save(user);
			return user;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Update
	 */

	/**
	 * IF the order is updated then some of the productline will be removed some will added
	 * to the order. So for removed one we will add stock back to product & from
	 * added one we will deduct the stock from product.
	 *
	 */
	@PreAuthorize("hasAuthority('USER')")
	@Transactional
	public Order updateOrder(Long orderId, OrderInput orderInput) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String username = userDetails.getUsername();
			Optional<User> optUserAuth = userRepository.findByEmail(username);

			if (optUserAuth.isEmpty()) {
				return null;
			}

			Order order = orderRepository.findById(orderId).orElse(null);
			if (order == null) {
				return null;
			}

			if (optUserAuth.get().getRoles().contains(Role.USER)) {
				if (optUserAuth.get().getId() != order.getUser().getId()) {
					return null;
				}
			}

			// set back to stock

			List<ProductLine> oldList = order.getProductLines();
			List<ProductLine> newList = productLineRepository.findAllById(orderInput.getProductLineIds());

			List<ProductLine> releaseList = new ArrayList<>(), newAddedList = new ArrayList<>();

			Set<Long> newListIds = newList.stream().map(ProductLine::getId).collect(Collectors.toSet());
			Set<Long> oldListIds = oldList.stream().map(ProductLine::getId).collect(Collectors.toSet());
			// Identify product lines to be released

			releaseList.addAll(
					oldList.stream().filter(pl -> !newListIds.contains(pl.getId())).collect(Collectors.toList()));

			// Identify newly added product lines
			newAddedList.addAll(
					newList.stream().filter(pl -> !oldListIds.contains(pl.getId())).collect(Collectors.toList()));

			for (ProductLine pl : releaseList) {
				pl.getProduct().setStock(pl.getProduct().getStock() + pl.getQuantity());
				productRepository.save(pl.getProduct());
				pl.setOrder(null);
			}
			productLineRepository.saveAll(oldList);

			for (ProductLine pl : newAddedList) {
				pl.getProduct().setStock(pl.getProduct().getStock() - pl.getQuantity());
				if (pl.getProduct().getStock() < 0) {
					return null;
				}
				productRepository.save(pl.getProduct());
				pl.setOrder(order);
			}

			productLineRepository.saveAll(newList);

			return orderRepository.save(order);

		} catch (Exception e) {
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@Transactional
	public Product updateProduct(Long productId, ProductInput productInput) {
		try {
			Product product = productRepository.findById(productId).orElse(null);
			if (product == null) {
				return null;
			}
			if (productInput.getName() != null) {
				product.setName(productInput.getName());
			}
			if (productInput.getPrice() >= 0) {
				product.setPrice(productInput.getPrice());
			}
			if (productInput.getStock() >= 0) {
				product.setStock(productInput.getStock());
			}

			product = productRepository.save(product);
			return product;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * While updating a product line if it is assigned to an order then the quantity of
	 * product should be affected.
	 */
	@PreAuthorize("hasAuthority('USER')")
	@Transactional
	public ProductLine updateProductLine(Long productLineId, ProductLineInput productLineInput) {
		try {
			ProductLine productLine = productLineRepository.findById(productLineId).orElse(null);
			if (productLine == null) {
				return null;
			}

			if (productLineInput.getQuantity() >= 0) {
				int productQuantityChanged = productLine.getQuantity() - productLineInput.getQuantity();
				Product tp = productLine.getProduct();
				tp.setStock(tp.getStock() + productQuantityChanged);
				if (tp.getStock() < 0) {
					return null;
				}
				productRepository.save(tp);

				productLine.setQuantity(productLine.getQuantity());
			} else {
				return null;
			}

			productLine = productLineRepository.save(productLine);
			return productLine;
		} catch (Exception e) {
			return null;
		}
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@Transactional
	public User updateUser(Long userId, UserInput userInput) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			return null;
		}

		if(!user.getEmail().equalsIgnoreCase(userInput.getEmail())) {
			return null;
		}
		if (userInput.getEmail() != null) {
			user.setEmail(userInput.getEmail());
		}
		if (userInput.getName() != null) {
			user.setName(userInput.getName());
		}
		if (userInput.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(userInput.getPassword()));
		}
		user.setRoles(userInput.getRoles());
		try {
			userRepository.save(user);
			return user;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Delete Section
	 */

	/**
	 * If a order is deleted then all quantity of the products of the productline
	 * will be move back to the product stock.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@Transactional
	public boolean deleteOrder(Long orderId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		Optional<User> optUserAuth = userRepository.findByEmail(username);

		if (optUserAuth.isEmpty()) {
			return false;
		}

		Optional<Order> optorder = orderRepository.findById(orderId);
		if (optorder.isEmpty()) {
			return false;
		}

		try {
			List<ProductLine> productLines = productLineRepository.findAllBy_order(optorder.get());
			for (ProductLine pLine : productLines) {
				Product tp = pLine.getProduct();
				tp.setStock(tp.getStock() + pLine.getQuantity());
				pLine.setOrder(null);
				productRepository.save(tp);
			}
			productLineRepository.saveAll(productLines);

			productRepository.deleteById(orderId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@Transactional
	public boolean deleteProduct(Long productId) {
		Optional<Product> optprod = productRepository.findById(productId);
		if (optprod.isEmpty()) {
			return false;
		}
		try {
			productRepository.deleteById(productId);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * If the productline is assign with an order then on delete of that productline
	 * the stock should increase.
	 */
	@PreAuthorize("hasAuthority('USER')")
	@Transactional
	public boolean deleteProductLine(Long productLineId) {
		Optional<ProductLine> optprodline = productLineRepository.findById(productLineId);
		if (optprodline.isEmpty()) {
			return false;
		}
		try {
			int quantity = optprodline.get().getQuantity();

			Optional<Product> optProd = productRepository.findById(productLineId);
			if (optProd.isEmpty()) {
				return false;
			}

			// if the productline is assign with an order
			if (optprodline.get().getOrder() != null) {
				optProd.get().setStock(optProd.get().getStock() + quantity);
				productRepository.save(optProd.get());
			}
			productLineRepository.deleteById(productLineId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@Transactional
	public boolean deleteUser(Long userId) {
		Optional<User> optuser = userRepository.findById(userId);
		if (optuser.isEmpty()) {
			return false;
		}
		try {
			userRepository.deleteById(userId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void createDefaultUser() {
		Optional<User> optUser = userRepository.findByEmail("admin@gmail.com");
		if (optUser.isEmpty()) {
			User user = new User();
			user.setEmail("admin@gmail.com");
			user.setName("Admin User");
			user.setPassword(passwordEncoder.encode("admin"));
			user.setRoles(Arrays.asList(Role.ADMIN, Role.USER));
			userRepository.save(user);
		}
		System.err.println("default admin user \nemail: admin@gmail.com \npassword:admin");

	}
}
