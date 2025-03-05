package me.jangluzniewicz.webstore.carts.services;

import me.jangluzniewicz.webstore.aws.interfaces.IAwsS3;
import me.jangluzniewicz.webstore.carts.mappers.CartMapper;
import me.jangluzniewicz.webstore.carts.repositories.CartRepository;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service("cartServiceNoCache")
@Validated
public class CartServiceNoCache extends AbstractCartService {
  public CartServiceNoCache(
      CartRepository cartRepository, CartMapper cartMapper, IProduct productService, IAwsS3 awsS3) {
    super(cartRepository, cartMapper, productService, awsS3);
  }
}
