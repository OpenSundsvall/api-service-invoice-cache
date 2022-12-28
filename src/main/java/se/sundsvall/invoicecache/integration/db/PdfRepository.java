package se.sundsvall.invoicecache.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;

import se.sundsvall.invoicecache.integration.db.entity.PdfEntity;

public interface PdfRepository extends JpaRepository<PdfEntity, Long> {
    PdfEntity findByFilename(String filename);
}
