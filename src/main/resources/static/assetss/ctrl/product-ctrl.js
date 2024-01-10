app.controller("product-ctrl", function($scope, $http) {
	var url = "/rest/products";
	var url1 = "/rest/categories";
	var url2 = "/rest/upload/images";

	$scope.items = [];
	$scope.cates = [];
	$scope.form = {};
	$scope.pageSize = 5;

	var sweetAlert = function(text, icon = "success") {
		Swal.fire({
			icon: icon,
			title: text,
			showConfirmButton: false,
			timer: 2000,
		});
	};

	var showErrorAlert = function(text) {
		sweetAlert(text, "error");
	};

	$scope.initialize = function() {
		// Load products
		$http.get(url).then(resp => {
			$scope.items = resp.data.map(item => {
				item.createDate = new Date(item.createDate);
				return item;
			});
		});

		// Load categories
		$http.get(url1).then(resp => {
			$scope.cates = resp.data;
		});
	};

	// Initialization
	$scope.initialize();

	// Reset form
	$scope.reset = function() {
		$scope.form = {
			createDate: new Date(),
			image: 'cloud-upload.jpg',
			available: true,
		};
	};

	// Display on form
	$scope.edit = function(item) {
		$scope.form = angular.copy(item);
		$(".nav-tabs a:eq(0)").tab('show');
	};

	// Create a new product
	$scope.create = function() {
		var item = angular.copy($scope.form);
		$http.post(url, item).then(resp => {
			resp.data.createDate = new Date(resp.data.createDate);
			$scope.items.push(resp.data);
			$scope.reset();
			sweetAlert("Thêm mới thành công!");
		}).catch(error => {
			showErrorAlert("Lỗi thêm mới sản phẩm!");
			console.log("Error", error);
		});
	};

	// Update a product
	$scope.update = function() {
		var item = angular.copy($scope.form);
		$http.put(`${url}/${item.id}`, item).then(resp => {
			var index = $scope.items.findIndex(p => p.id == item.id);
			$scope.items[index] = item;
			$scope.reset();
			sweetAlert("Cập nhật sản phẩm thành công!");
		}).catch(error => {
			showErrorAlert("Lỗi cập nhật sản phẩm!");
			console.log("Error", error);
		});
	};

	// Delete a product
	$scope.delete = function(item) {
		$http.delete(`${url}/${item.id}`).then(resp => {
			var index = $scope.items.findIndex(p => p.id == item.id);
			$scope.items.splice(index, 1);
			$scope.reset();
			sweetAlert("Xóa sản phẩm thành công!");
		}).catch(error => {
			showErrorAlert("Lỗi xóa sản phẩm!");
			console.log("Error", error);
		});
	};

	// Upload image
	$scope.imageChanged = function(files) {
		var data = new FormData();
		data.append('file', files[0]);
		$http.post(url2, data, {
			transformRequest: angular.identity,
			headers: { 'Content-Type': undefined }
		}).then(resp => {
			if (resp.data && resp.data.name) {
				$scope.form.image = resp.data.name;
			} else {
				showErrorAlert("Lỗi tải lên hình ảnh!");
			}
		}).catch(error => {
			showErrorAlert("Lỗi tải lên hình ảnh!");
			console.log("Error", error);
		});
	};

	// Pagination
	$scope.pager = {
		page: 0,
		size: $scope.pageSize,
		get items() {
			var start = this.page * this.size;
			return $scope.items.slice(start, start + this.size);
		},
		get count() {
			return Math.ceil($scope.items.length / this.size);
		},
		first() {
			this.page = 0;
		},
		prev() {
			this.page = Math.max(0, this.page - 1);
		},
		next() {
			this.page = Math.min(this.page + 1, this.count - 1);
		},
		last() {
			this.page = this.count - 1;
		}
	};
});
