import * as React from 'react';
import { DataGrid, GridColDef, GridRowsProp } from '@mui/x-data-grid';
import { Box, Button, Stack, Typography } from '@mui/material';
import { useDemoData } from '@mui/x-data-grid-generator';
import { useState, useEffect } from 'react';

import AddProductDialog from '../components/AddProductDialog';
import apiInstance from '../api/instance';
import OwnProductsResponse from '../types/OwnProductsResponse';
import IResponse from '../types/IResponse';

export default function Product() {
    const [totalPage, setTotalPage] = useState(0);
    const [addProductDialog, setAddProductDialog] = useState(false);
    
    function openAddProduct() {
        setAddProductDialog(true);
    }

    function closeAddProduct() {
        setAddProductDialog(false);
    }

    const columns: GridColDef[] = [
        {
            "field": "id",      
            "headerName": "ID",      
            "hideable": true
        },
        {
            "field" : "name",
            "headerName" : "이름"
        },
        {
            field: "manufacturer",
            headerName: "제조사"
        },
        {
            field: "description",
            headerName: "설명"
        },        
        {
            field: "test",
            headerName: "test",
            width: 200,
            renderCell: (params) => (
                <Button variant='contained'>{params.row.test}</Button>
            )
        }
    ]

    const [paginationModel, setPaginationModel] = useState({
        page: 0,
        pageSize: 10,
    });

    const [rows, setRows] = React.useState<GridRowsProp>([]);
    const [loading, setLoading] = React.useState(false);

    useEffect(() => {
        // let active = true;
        setLoading(true);

        apiInstance.get<IResponse<OwnProductsResponse>>(`/api/products?page=${paginationModel.page}`)
        .then(d => {                        
            setTotalPage(d.data.data.totalCount);                        
            setRows(d.data.data.products.map(product => {
                return {
                    id: product.id,
                    name: product.name,
                    manufacturer: product.manufacturer,
                    description: product.description,
                    attributes: product.attributes
                }
            }))
            setLoading(false);
        });

        
        // (async () => {
            
        //     await new Promise((resolve) => setTimeout(resolve, 300));
        //     if (!active) {
        //         return;
        //     }
            
        // })();

        // return () => {
        //     active = false;
        // };
    }, [paginationModel.page]);

    return (
        <Box overflow={"scroll"}>
            <Stack direction={"column"} spacing={5}>
                <Typography variant="h4" width={"100%"} marginBottom={3}>프로필</Typography>

                <Typography variant="h5" width={"100%"} marginBottom={3}>상품목록</Typography>
                <Button variant='contained' onClick={openAddProduct}>상품 추가</Button>
                <AddProductDialog open={addProductDialog} close={closeAddProduct}/>
                <DataGrid
                    disableRowSelectionOnClick                    
                    columns={columns}
                    rows={rows}

                    pagination
                    pageSizeOptions={[10]}
                    rowCount={totalPage}
                    loading={loading}
                    paginationMode="server"

                    paginationModel={paginationModel}
                    onPaginationModelChange={setPaginationModel}
                />

                <Typography variant="h5" width={"100%"} marginBottom={3}>상세 상품목록</Typography>
                <Button variant='contained'>상세 상품 추가</Button>            
            </Stack>
        </Box>
    );
}