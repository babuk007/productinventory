/**
 * Product Inventory Management - Legacy jQuery Application
 */

$(document).ready(function() {
    var categories = [];
    var currentEditingProductId = null;

    // Initial Data Fetch
    init();

    function init() {
        loadCategories();
        loadDashboardStats();
        loadProducts();
        loadRecentTransactions();
    }

    // ----------------------------------------------------
    // Category Loading
    // ----------------------------------------------------
    function loadCategories() {
        $.ajax({
            url: '/api/categories',
            method: 'GET',
            success: function(data) {
                categories = data;
                var filterSelect = $('#filter-category');
                var formSelect = $('#product-category');

                filterSelect.find('option:not(:first)').remove();
                formSelect.empty();

                $.each(data, function(index, cat) {
                    filterSelect.append($('<option>', {
                        value: cat.id,
                        text: cat.name
                    }));
                    formSelect.append($('<option>', {
                        value: cat.id,
                        text: cat.name + ' (' + cat.code + ')'
                    }));
                });
            },
            error: function(xhr) {
                showAlert('danger', 'Failed to load categories: ' + xhr.responseText);
            }
        });
    }

    // ----------------------------------------------------
    // Dashboard Stats Loading
    // ----------------------------------------------------
    function loadDashboardStats() {
        $.ajax({
            url: '/api/dashboard/stats',
            method: 'GET',
            success: function(stats) {
                $('#stat-total-products').text(stats.totalProducts);
                $('#stat-total-quantity').text(stats.totalQuantity.toLocaleString());
                $('#stat-inventory-value').text('$' + stats.totalInventoryValue.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2}));
                $('#stat-low-stock').text(stats.lowStockCount);
                $('#stat-out-of-stock').text(stats.outOfStockCount);
            },
            error: function(xhr) {
                console.error('Failed to load dashboard stats', xhr);
            }
        });
    }

    // ----------------------------------------------------
    // Products Table Loading
    // ----------------------------------------------------
    function loadProducts() {
        var searchQuery = $('#filter-search').val();
        var categoryId = $('#filter-category').val();
        var status = $('#filter-status').val();

        var params = {};
        if (searchQuery) params.search = searchQuery;
        if (categoryId) params.categoryId = categoryId;
        if (status) params.status = status;

        $('#products-tbody').html('<tr><td colspan="8" class="text-center text-muted" style="padding: 30px;"><i class="glyphicon glyphicon-refresh glyphicon-spin"></i> Loading inventory data...</td></tr>');

        $.ajax({
            url: '/api/products',
            method: 'GET',
            data: params,
            success: function(products) {
                renderProductTable(products);
            },
            error: function(xhr) {
                $('#products-tbody').html('<tr><td colspan="8" class="text-center text-danger">Error loading inventory data.</td></tr>');
            }
        });
    }

    function renderProductTable(products) {
        var tbody = $('#products-tbody');
        tbody.empty();

        if (!products || products.length === 0) {
            tbody.html('<tr><td colspan="8" class="text-center text-muted" style="padding: 30px;">No inventory items matched your criteria.</td></tr>');
            return;
        }

        $.each(products, function(i, product) {
            var statusBadge = '';
            if (product.status === 'IN_STOCK') {
                statusBadge = '<span class="label label-success label-in-stock"><i class="glyphicon glyphicon-ok-sign"></i> In Stock</span>';
            } else if (product.status === 'LOW_STOCK') {
                statusBadge = '<span class="label label-warning label-low-stock"><i class="glyphicon glyphicon-warning-sign"></i> Low Stock</span>';
            } else {
                statusBadge = '<span class="label label-danger label-out-of-stock"><i class="glyphicon glyphicon-remove-sign"></i> Out of Stock</span>';
            }

            var categoryName = product.category ? product.category.name : '-';
            var formattedPrice = '$' + (product.price ? product.price.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2}) : '0.00');

            var row = $('<tr>');
            row.append('<td><strong>' + escapeHtml(product.sku) + '</strong></td>');
            row.append('<td><strong>' + escapeHtml(product.name) + '</strong><br><small class="text-muted">' + escapeHtml(product.description || '') + '</small></td>');
            row.append('<td><span class="badge" style="background:#475569;">' + escapeHtml(categoryName) + '</span></td>');
            row.append('<td>' + escapeHtml(product.location || 'N/A') + '</td>');
            row.append('<td class="text-right"><strong>' + formattedPrice + '</strong></td>');
            row.append('<td class="text-center"><span style="font-size: 14px; font-weight: bold;">' + product.quantity + '</span> <small class="text-muted">(Min: ' + product.minThreshold + ')</small></td>');
            row.append('<td class="text-center">' + statusBadge + '</td>');

            var actionsTd = $('<td class="text-right action-btn-group">');
            var adjustBtn = $('<button class="btn btn-xs btn-default" title="Adjust Stock"><i class="glyphicon glyphicon-transfer"></i> Stock</button>')
                .click(function() { openStockModal(product); });
            var editBtn = $('<button class="btn btn-xs btn-info" title="Edit Product"><i class="glyphicon glyphicon-pencil"></i></button>')
                .click(function() { openEditModal(product); });
            var deleteBtn = $('<button class="btn btn-xs btn-danger" title="Delete Product"><i class="glyphicon glyphicon-trash"></i></button>')
                .click(function() { confirmDelete(product); });

            actionsTd.append(adjustBtn).append(editBtn).append(deleteBtn);
            row.append(actionsTd);

            tbody.append(row);
        });
    }

    // ----------------------------------------------------
    // Audit Transactions Loading
    // ----------------------------------------------------
    function loadRecentTransactions() {
        $.ajax({
            url: '/api/dashboard/transactions',
            method: 'GET',
            success: function(transactions) {
                var container = $('#audit-feed');
                container.empty();

                if (!transactions || transactions.length === 0) {
                    container.html('<div class="text-muted text-center" style="padding: 20px;">No recent transactions.</div>');
                    return;
                }

                $.each(transactions, function(i, tx) {
                    var item = $('<div class="audit-item ' + tx.transactionType + '">');
                    var dateFormatted = tx.timestamp ? new Date(tx.timestamp).toLocaleString() : 'Just now';
                    var changeText = tx.quantityChange > 0 ? '+' + tx.quantityChange : tx.quantityChange;

                    var header = $('<div class="audit-title">')
                        .html('<span class="label label-default">' + tx.transactionType + '</span> ' + escapeHtml(tx.product.name) + ' (' + changeText + ')');
                    var meta = $('<div class="audit-time">').text(dateFormatted + ' | Bal: ' + tx.remainingQuantity);
                    var desc = $('<div class="audit-desc">').text(tx.reason || '');

                    item.append(header).append(meta).append(desc);
                    container.append(item);
                });
            }
        });
    }

    // ----------------------------------------------------
    // Search & Filter Events
    // ----------------------------------------------------
    var searchTimer;
    $('#filter-search').on('keyup', function() {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(loadProducts, 300);
    });

    $('#filter-category, #filter-status').on('change', function() {
        loadProducts();
    });

    $('#btn-refresh').on('click', function() {
        init();
        showAlert('info', 'Inventory data refreshed.');
    });

    // ----------------------------------------------------
    // Add / Edit Product Modal
    // ----------------------------------------------------
    $('#btn-add-product').on('click', function() {
        currentEditingProductId = null;
        $('#product-modal-title').text('Register New Inventory Item');
        $('#product-form')[0].reset();
        $('#product-id').val('');
        $('#product-modal').modal('show');
    });

    function openEditModal(product) {
        currentEditingProductId = product.id;
        $('#product-modal-title').text('Edit Inventory Item: ' + product.sku);
        $('#product-id').val(product.id);
        $('#product-sku').val(product.sku);
        $('#product-name').val(product.name);
        $('#product-description').val(product.description || '');
        if (product.category) {
            $('#product-category').val(product.category.id);
        }
        $('#product-price').val(product.price);
        $('#product-quantity').val(product.quantity);
        $('#product-threshold').val(product.minThreshold);
        $('#product-location').val(product.location || '');
        $('#product-modal').modal('show');
    }

    $('#product-form').on('submit', function(e) {
        e.preventDefault();

        var payload = {
            sku: $('#product-sku').val().trim(),
            name: $('#product-name').val().trim(),
            description: $('#product-description').val().trim(),
            categoryId: parseInt($('#product-category').val(), 10),
            price: parseFloat($('#product-price').val()),
            quantity: parseInt($('#product-quantity').val(), 10),
            minThreshold: parseInt($('#product-threshold').val(), 10),
            location: $('#product-location').val().trim()
        };

        var url = currentEditingProductId ? '/api/products/' + currentEditingProductId : '/api/products';
        var method = currentEditingProductId ? 'PUT' : 'POST';

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: function() {
                $('#product-modal').modal('hide');
                showAlert('success', 'Product saved successfully!');
                init();
            },
            error: function(xhr) {
                showAlert('danger', 'Failed to save product: ' + xhr.responseText);
            }
        });
    });

    // ----------------------------------------------------
    // Stock Adjustment Modal
    // ----------------------------------------------------
    function openStockModal(product) {
        $('#stock-product-id').val(product.id);
        $('#stock-product-name').text(product.name + ' (' + product.sku + ')');
        $('#stock-current-quantity').text(product.quantity);
        $('#stock-adjust-form')[0].reset();
        $('#stock-modal').modal('show');
    }

    $('#stock-adjust-form').on('submit', function(e) {
        e.preventDefault();

        var productId = $('#stock-product-id').val();
        var payload = {
            type: $('#stock-type').val(),
            quantity: parseInt($('#stock-quantity').val(), 10),
            reason: $('#stock-reason').val().trim()
        };

        $.ajax({
            url: '/api/products/' + productId + '/adjust-stock',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: function() {
                $('#stock-modal').modal('hide');
                showAlert('success', 'Stock adjustment recorded successfully!');
                init();
            },
            error: function(xhr) {
                showAlert('danger', 'Error updating stock: ' + xhr.responseText);
            }
        });
    });

    // ----------------------------------------------------
    // Delete Confirmation
    // ----------------------------------------------------
    function confirmDelete(product) {
        if (confirm('Are you sure you want to remove "' + product.name + '" (' + product.sku + ') from inventory?')) {
            $.ajax({
                url: '/api/products/' + product.id,
                method: 'DELETE',
                success: function() {
                    showAlert('warning', 'Product deleted from inventory.');
                    init();
                },
                error: function(xhr) {
                    showAlert('danger', 'Failed to delete product: ' + xhr.responseText);
                }
            });
        }
    }

    // ----------------------------------------------------
    // Utility Helpers
    // ----------------------------------------------------
    function showAlert(type, message) {
        var alertHtml = '<div class="alert alert-' + type + ' alert-dismissible" role="alert">' +
            '<button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>' +
            message +
            '</div>';
        $('#alert-container').html(alertHtml);
        setTimeout(function() {
            $('#alert-container .alert').fadeOut(500, function() { $(this).remove(); });
        }, 4000);
    }

    function escapeHtml(text) {
        if (!text) return '';
        return String(text)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }
});
