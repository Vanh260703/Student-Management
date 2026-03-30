import { Modal } from 'antd';

export const confirmDelete = (onConfirm: () => void, itemName = 'mục này') => {
  Modal.confirm({
    title: 'Xác nhận xoá',
    content: `Bạn có chắc chắn muốn xoá ${itemName}? Thao tác này không thể hoàn tác.`,
    okText: 'Xoá',
    okType: 'danger',
    cancelText: 'Huỷ',
    onOk: onConfirm,
  });
};
