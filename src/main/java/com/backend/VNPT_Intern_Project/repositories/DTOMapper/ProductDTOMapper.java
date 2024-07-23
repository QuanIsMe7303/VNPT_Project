package com.backend.VNPT_Intern_Project.repositories.DTOMapper;

import com.backend.VNPT_Intern_Project.dtos.ProductDTO.ProductDTORsponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ProductDTOMapper implements RowMapper<ProductDTORsponse> {

    private final JdbcTemplate jdbcTemplate;

    public ProductDTOMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ProductDTORsponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProductDTORsponse productDTORsponse = new ProductDTORsponse();
        productDTORsponse.setUuidProduct(rs.getString("p.uuid_product"));
        productDTORsponse.setTitle(rs.getString("p.title"));
        productDTORsponse.setMetaTitle(rs.getString("p.meta_title"));
        productDTORsponse.setSummary(rs.getString("p.summary"));
        productDTORsponse.setType(rs.getShort("p.type"));
        productDTORsponse.setPrice(rs.getDouble("p.price"));
        productDTORsponse.setQuantity(rs.getShort("p.quantity"));
        productDTORsponse.setCreatedDate(rs.getObject("p.created_date", LocalDateTime.class));
        productDTORsponse.setUpdatedDate(rs.getObject("p.updated_date", LocalDateTime.class));
        productDTORsponse.setPublishedDate(rs.getObject("p.published_date", LocalDateTime.class));
        productDTORsponse.setDescription(rs.getString("p.description"));

        // brand and category
        String brandSql = "SELECT b.name FROM product p " +
                "LEFT JOIN brand b ON p.uuid_brand = b.uuid_brand " +
                "WHERE p.uuid_product = ?";
        String brandName = jdbcTemplate.queryForObject(brandSql, new Object[]{rs.getString("p.uuid_product")}, String.class);
        productDTORsponse.setBrand(brandName);

        String categorySql = "SELECT c.title FROM product p " +
                "LEFT JOIN product_category pc ON pc.uuid_product = p.uuid_product " +
                "LEFT JOIN category c ON c.uuid_category = pc.uuid_category " +
                "WHERE p.uuid_product = ?";
        String categoryName = jdbcTemplate.queryForObject(categorySql, new Object[]{rs.getString("p.uuid_product")}, String.class);
        productDTORsponse.setCategory(categoryName);

        // other attributes
        String attributeSql = "SELECT a.key, pa.value FROM product p " +
                "LEFT JOIN product_attribute pa ON pa.uuid_product = p.uuid_product " +
                "LEFT JOIN attribute a ON pa.uuid_attribute = a.uuid_attribute " +
                "WHERE p.uuid_product = ?";

        Map<String, String> otherAttributes = new HashMap<>();

        jdbcTemplate.query(attributeSql, new Object[]{rs.getString("uuid_product")}, (ResultSet rs1) -> {
            String attributeKey = rs1.getString("a.key");
            String attributeValue = rs1.getString("pa.value");

            if (attributeKey != null && attributeValue != null) {
                otherAttributes.put(attributeKey, attributeValue);
            }
        });
        productDTORsponse.setOtherAttributes(otherAttributes);

        return productDTORsponse;
    }
}