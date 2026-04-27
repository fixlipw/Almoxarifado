package com.ufc.almoxarifado.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.ufc.almoxarifado.dto.EstoqueRequest;
import com.ufc.almoxarifado.dto.EstoqueResponse;
import com.ufc.almoxarifado.entity.Estoque;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.repository.EstoqueRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private static final DateTimeFormatter REPORT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String BRASAO_PATH = "static/images/brasao.png";

    private final EstoqueRepository estoqueRepository;
    private final Configuration freemarkerConfiguration;

    public EstoqueResponse create(EstoqueRequest request) {
        Estoque entity = new Estoque();
        applyRequest(entity, request);
        return toResponse(estoqueRepository.save(entity));
    }

    public List<EstoqueResponse> findAll() {
        return estoqueRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Page<EstoqueResponse> findAllPaginated(int page, int size) {
        return estoqueRepository.findAllPaginated(PageRequest.of(page, size).withSort(Sort.by("nome").ascending()))
                .map(this::toResponse);
    }

    public EstoqueResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    public EstoqueResponse update(UUID id, EstoqueRequest request) {
        Estoque entity = getEntity(id);

        if (Boolean.FALSE.equals(request.isAtivado()) && Boolean.TRUE.equals(entity.getIsAtivado())) {
            boolean hasActiveOrders = estoqueRepository.existsActiveOrdersByEstoqueId(id);
            if (hasActiveOrders) {
                throw new IllegalStateException("Nao é possível atualizar este item, pois ele está associado a pedidos ativos.");
            }
        }

        applyRequest(entity, request);
        return toResponse(estoqueRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!estoqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estoque nao encontrado para id: " + id);
        }
        estoqueRepository.deleteById(id);
    }

    public Estoque getEntity(UUID id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque nao encontrado para id: " + id));
    }

    private void applyRequest(Estoque entity, EstoqueRequest request) {
        entity.setNome(request.nome());
        entity.setQuantidadeDisponivel(request.quantidadeDisponivel());
        entity.setQuantidade(request.quantidade());
        entity.setTipo(request.tipo());
        if (request.isAtivado() != null) {
            entity.setIsAtivado(request.isAtivado());
        }
    }

    private EstoqueResponse toResponse(Estoque entity) {
        return new EstoqueResponse(
                entity.getId(),
                entity.getNome(),
                entity.getQuantidade(),
                entity.getQuantidadeDisponivel(),
                entity.getTipo(),
                entity.getIsAtivado()
        );
    }

    public byte[] generateItemReport(String format) {
        if (!"pdf".equalsIgnoreCase(format)) {
            throw new IllegalArgumentException("Formato nao suportado: " + format);
        }

        try {
            List<Map<String, Object>> itens = estoqueRepository.findAll().stream()
                    .map(item -> {
                        Map<String, Object> row = new HashMap<>();
                        row.put("nome", item.getNome());
                        row.put("quantidade", item.getQuantidade());
                        row.put("tipo", item.getTipo() != null ? item.getTipo().name() : "");
                        row.put("status", Boolean.TRUE.equals(item.getIsAtivado()) ? "Ativo" : "Inativo");
                        return row;
                    })
                    .toList();

            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Relatório de Estoque");
            model.put("geradoEm", REPORT_DATE_FORMAT.format(LocalDateTime.now()));
            model.put("totalItens", itens.size());
            model.put("itens", itens);
            model.put("brasaoUfc", loadBrasaoDataUri());

            Template template = freemarkerConfiguration.getTemplate("relatorios/estoque.ftlh");
            String html;
            try (StringWriter writer = new StringWriter()) {
                template.process(model, writer);
                html = writer.toString();
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(html, null);
                builder.toStream(outputStream);
                builder.run();
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de estoque: " + e.getMessage(), e);
        }
    }

    private String loadBrasaoDataUri() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(BRASAO_PATH);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                throw new RuntimeException("Imagem do brasão nao encontrada em " + BRASAO_PATH);
            }

            inputStream.transferTo(outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar imagem do brasão: " + e.getMessage(), e);
        }
    }
}
