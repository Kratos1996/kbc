import swaggerJSDoc from 'swagger-jsdoc';
import path from 'path';

export const swaggerSpec = swaggerJSDoc({
  definition: {
    openapi: '3.0.3',
    info: { title: 'KBC Crorepati Quiz API', version: '0.1.0' },
    servers: [{ url: 'http://localhost:4000' }],
    components: {
      securitySchemes: {
        bearerAuth: { type: 'http', scheme: 'bearer', bearerFormat: 'JWT' },
      },
    },
  },
  apis: [path.join(__dirname, '..', 'routes', '*.ts'), path.join(__dirname, '..', 'routes', '*.js')],
});
