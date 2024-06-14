import * as React from 'react';
import { DataGrid, GridColDef, GridRowsProp } from '@mui/x-data-grid';
import { Box, Button, Stack, Typography } from '@mui/material';
import { useDemoData } from '@mui/x-data-grid-generator';
import { useState } from 'react';
import AddProductDialog from '../components/AddProductDialog';

export const fetchData = async (pageSize: number, page: number) => {
    try {
        return {
            data: [],
            total: 10
        };
    } catch (error) {
        console.error('Error fetching data:', error);
        throw error;
    }
};

export default function Product() {
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
            "hideable": true
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

    const { data } = useDemoData({
        dataSet: 'Employee',
        rowLength: 100,
        maxColumns: 6,
    });

    const [paginationModel, setPaginationModel] = React.useState({
        page: 0,
        pageSize: 5,
    });

    const [rows, setRows] = React.useState<GridRowsProp>([]);
    const [loading, setLoading] = React.useState(false);

    React.useEffect(() => {
        let active = true;

        (async () => {
            setLoading(true);
            await new Promise((resolve) => setTimeout(resolve, 300));
            if (!active) {
                return;
            }

            setRows([
                {
                    id: "1",
                    test: "test"
                }
            ]);
            setLoading(false);
        })();

        return () => {
            active = false;
        };
    }, [paginationModel.page, data]);

    return (
        <Box overflow={"scroll"}>
            <Stack direction={"column"} spacing={5}>
                <Typography variant="h4" width={"100%"} marginBottom={3}>프로필</Typography>

                <Typography variant="h5" width={"100%"} marginBottom={3}>상품목록</Typography>
                <Button variant='contained' onClick={() => setAddProductDialog(true)}>상품 추가</Button>
                <AddProductDialog open={addProductDialog} close={closeAddProduct}/>
                <DataGrid
                    disableRowSelectionOnClick                    
                    columns={columns}
                    rows={rows}

                    pagination
                    pageSizeOptions={[5]}
                    rowCount={100}
                    loading={loading}
                    paginationMode="server"

                    paginationModel={paginationModel}
                    onPaginationModelChange={setPaginationModel}
                />

                <Typography variant="h5" width={"100%"} marginBottom={3}>상세 상품목록</Typography>
                <Button variant='contained'>상세 상품 추가</Button>
                <DataGrid
                    disableRowSelectionOnClick                    
                    columns={columns}
                    rows={rows}

                    pagination
                    pageSizeOptions={[5]}
                    rowCount={100}
                    loading={loading}
                    paginationMode="server"

                    paginationModel={paginationModel}
                    onPaginationModelChange={setPaginationModel}
                />

            </Stack>
        </Box>
    );
}
