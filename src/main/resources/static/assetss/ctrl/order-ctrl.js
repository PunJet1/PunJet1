app.controller("order-ctrl", function($scope, $http) {
	$scope.items = [];
	$scope.itemss = [];
	$scope.form = {};
	$scope.form2 = {};

	var sweetalert = function(text) {
		Swal.fire({
			icon: "success",
			title: text,
			showConfirmButton: false,
			timer: 2000,
		});
	}

	var swarning = function(text) {
		Swal.fire({
			icon: "error",
			title: text,
			showConfirmButton: false,
			timer: 2000,
		});
	}
	$scope.orderStatuses = [
		{ id: 'PROCESSING', displayName: 'Đang xử lý' },
		{ id: 'CONFIRMED', displayName: 'Đã xử lý' },
		{ id: 'CANCELED', displayName: 'Hủy đơn' },
		// Thêm các đối tượng trạng thái khác nếu cần
	];

	$scope.initiable = function() {

		//Load order
		$http.get("/rest/orders").then(resp => {
			$scope.items = resp.data;
			$scope.items.forEach(item => {
				item.createDate = new Date(item.createDate)
			})
			console.log(resp.data);
		});

		//Load orderdetail
		$http.get("/rest/orderdetails").then(resp => {
			$scope.itemss = resp.data;
			console.log(resp.data);
		});

		//Load product
		$http.get("/rest/products").then(resp => {
			$scope.products = resp.data;
			console.log(resp.data);
		});
		//Load account
		$http.get("/rest/accounts").then(resp => {
			$scope.itemss = resp.data;
			console.log(resp.data);
		});

	}

	//Khởi đầu
	$scope.initiable();

	//xoa form
	$scope.reset = function() {
		$scope.form = {
			//			createDate: new Date(),
			ivailable: true,
		};
	}

	//hien thi len form
	$scope.edit = function(item) {
		$scope.form = angular.copy(item);
		$(".nav-tabs a:eq(0)").tab('show');
	}

	//them sp moi
	$scope.create = function() {
	}

	$scope.update = function() {
		var item = angular.copy($scope.form);

		// Kiểm tra xem item và item.status có tồn tại
		if (item && item.status) {
			// Thêm logic để xử lý cập nhật trạng thái đơn hàng
			item.status = item.status.id;

			// Kiểm tra nếu đơn hàng ở trạng thái "Hủy đơn" thì chỉ cho phép cập nhật trạng thái "Hủy đơn"
			if (item.status === 'CANCELED') {
				updateOrder(item);
			} else {
				// Kiểm tra chuyển từ "Đang xử lý" sang "Đã xử lý"
				if (item.status === 'PROCESSING') {
					// Chỉ cho phép chuyển từ "Đang xử lý" sang "Đã xử lý" nếu trạng thái hiện tại là "PROCESSING"
					if ($scope.form.status && $scope.form.status.id === 'CONFIRMED') {
						checkAndChangeStatus(item);
					} else {
						swarning("Không thể chuyển từ 'Đã xử lý' sang 'Đang xử lý'.");
					}
				} else {
					// Các trường hợp khác có thể cập nhật bình thường
					updateOrder(item);
				}
			}
		} else {
			swal("Lỗi: Đơn hàng hoặc trạng thái không hợp lệ.");
		}
	};

	function updateOrder(item) {
		$http.put(`/rest/orders/${item.id}`, item).then(resp => {
			var index = $scope.items.findIndex(p => p.id == item.id);
			$scope.items[index] = item;
			$scope.reset();
			sweetalert("Cập nhật đơn hàng thành công!");
		}).catch(error => {
			swarning("Lỗi cập nhật đơn hàng!");
			console.log("Lỗi", error);
		});
	}

	function checkAndChangeStatus(item) {
		var originalStatus = $scope.items.find(p => p.id == item.id)?.status;

		if (originalStatus !== undefined && originalStatus !== null) {
			// Kiểm tra xem có phải chuyển từ "Đang xử lý" sang "Đã xử lý" không
			if (originalStatus === 'CONFIRMED') {
				updateOrder(item);
			} else {
				swarning("Không thể chuyển từ 'Đã xử lý' sang 'Đang xử lý'.");
			}
		} else {
			swarning("Lỗi: Không tìm thấy trạng thái hiện tại của đơn hàng.");
		}
	}

	//xoa sp
	//	$scope.delete = function(item) {
	//		$http.delete(`/rest/orders/${item.id}`).then(resp => {
	//			var index = $scope.items.findIndex(p => p.id == item.id);
	//			$scope.items.splice(index, 1);
	//			$scope.reset();
	//			sweetalert("Xóa sản phẩm thành công!");
	//		}).catch(error => {
	//			swarning("Lỗi xóa sản phẩm!");
	//			console.log("Error", error);
	//		});
	//	}

	//phan trang
//	$scope.pager = {
//		page: 0,
//		size: $scope.pageSize,
//		get items() {
//			var start = this.page * this.size;
//			return $scope.items.slice(start, start + this.size);
//		},
//		get count() {
//			return Math.ceil($scope.items.length / this.size);
//		},
//		first() {
//			this.page = 0;
//		},
//		prev() {
//			this.page = Math.max(0, this.page - 1);
//		},
//		next() {
//			this.page = Math.min(this.page + 1, this.count - 1);
//		},
//		last() {
//			this.page = this.count - 1;
//		}
//	};

})